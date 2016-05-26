package org.cora.maths.collision;

import java.util.ArrayList;

import org.cora.maths.Circle;
import org.cora.maths.FloatA;
import org.cora.maths.Form;
import org.cora.maths.Matrix2;
import org.cora.maths.RoundForm;
import org.cora.maths.Vector2D;


public class CollisionDetectorTools
{
    public static void getPushVector(AxesSat axesSat, Vector2D push, FloatA t)
    {
        t.v = 0f;
        boolean found = false;
        for (int i = 0; i < axesSat.axesT.size(); i++)
        {
            if (axesSat.tAxesT.get(i) > t.v)
            {
                t.v = axesSat.tAxesT.get(i);
                push.set(axesSat.axesT.get(i));
                found = true;
            }
        }
        push.normalize();

        if (found)
            return;

        float magnitude1 = axesSat.axes.get(0).normalize();
        axesSat.tAxes.set(0, axesSat.tAxes.get(0) / magnitude1);
        t.v = axesSat.tAxes.get(0);
        push.set(axesSat.axes.get(0));

        for (int i = 1; i < axesSat.axes.size(); i++)
        {
            float magnitude = axesSat.axes.get(i).normalize();
            axesSat.tAxes.set(i, axesSat.tAxes.get(i) / magnitude);
            if (axesSat.tAxes.get(i) > t.v)
            {
                t.v = axesSat.tAxes.get(i);
                push.set(axesSat.axes.get(i));
                found = true;
            }
        }
    }
    

    public static Vector2D handleEdgePoint(Vector2D PA, Vector2D PB1,
            Vector2D PB2)
    {
        Vector2D edgeB = new Vector2D(PB1, PB2);
        Vector2D projection = new Vector2D(PB1, PB2);
        float fProjection = edgeB.scalarProduct(projection);

        return edgeB.multiply(fProjection);
    }
    
    public static ArrayList<Vector2D> getVectorsSatLocal(Form A, Form B, Matrix2 orient, Matrix2 orientI)
    {
        if (! (A instanceof RoundForm))
        {
            return getVectorsSatForm(A);
        }
        
        if (A instanceof Circle)
        {
            Circle cA = (Circle) A;
            
            if (! (B instanceof RoundForm))
            {
                return getVectorsSatCircleForm(cA, B, orient, orientI);
            }
            
            if (B instanceof Circle)
            {
                Circle cB = (Circle) B;
                return getVectorsSatCircleCircle(cA, cB);
            }
            // Autre que circle
            assert(false);
        }
        // Autre que circle
        assert(false);
        return null;
    }

    public static ArrayList<Vector2D> getVectorsSatCircleForm(Circle A, Form B, Matrix2 transformAB, Matrix2 transformBA)
    {
        ArrayList<Vector2D> l_vectors = new ArrayList<Vector2D>(1);
        
        // On recherche le point le plus proche de l'autre forme
        // --OPTI-- On convertit le centre du cercle dans le repère local de l'autre forme
        Vector2D centerAinB = B.getOrientation().inverse().multiply(A.getCenter());
        
        Vector2D min = new Vector2D();
        float distMin = Float.MAX_VALUE;
        
        for (int i = 0; i < B.size(); i++)
        {
            // --OPTI-- Utilisation du sqMagnitude au lieu de magnitude plus long à calculer            
            Vector2D tempV = centerAinB.sub(B.getLocal(i));
            float temp = tempV.getSqMagnitude();
            
            if (temp < distMin)
            {
                distMin = temp;
                min.set(tempV);
            }
        }

        
        // On retransforme le vecteur dans le repère de A
        Vector2D res = transformBA.multiply(min);
        res.z = 0;
        l_vectors.add(res);
        
        return l_vectors;
    }
    
    public static ArrayList<Vector2D> getVectorsSatCircleCircle(Circle A, Circle B)
    {
        ArrayList<Vector2D> l_vectors = new ArrayList<Vector2D>(1);
        l_vectors.add(new Vector2D(A.getCenter(), B.getCenter()));
        return l_vectors;
    }
    
    public static ArrayList<Vector2D> getVectorsSatForm(Form A)
    {
        ArrayList<Vector2D> l_vectors = new ArrayList<Vector2D>(A.size());
        for (int j = A.size() - 1, i = 0; i < A.size(); j = i, i++)
        {
            Vector2D v = new Vector2D(A.get(j), A.get(i));
            l_vectors.add(v.getPerpendicular());
        }

        // On enleve les vectors colinéaires
        for (int i = 0; i < l_vectors.size() - 1; i++)
        {
            // Si on est en dessous de 2 vecteurs ca sert à rien de continuer,
            // On sait que ces 2 vecteurs (ou moins) ne sont pas colinéaires
            if (l_vectors.size() < 3)
                break;

            for (int j = i + 1; j < l_vectors.size(); j++)
            {
                if (l_vectors.get(i).isColinear(l_vectors.get(j)))
                {
                    l_vectors.remove(i);
                    i--;
                    break;
                }
            }
        }
        return l_vectors;
    }
}

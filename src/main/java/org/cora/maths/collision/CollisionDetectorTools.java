package org.cora.maths.collision;

import org.cora.maths.*;

import java.util.ArrayList;


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

        System.out.println("Need this? Really?");

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
        return A.getSavedVectorsLocal();
    }
}

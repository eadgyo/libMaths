package org.cora.maths.collision;

import java.util.ArrayList;

import org.cora.maths.FloatA;
import org.cora.maths.Form;
import org.cora.maths.Matrix2;
import org.cora.maths.Vector2D;

/**
 * Tools to detect collision between two convex polygons
 */
public class CollisionDetector
{
    /**
     *
     * @param A
     * @param B
     * @param VA velocity of object A
     * @param VB velocity of object B
     * @param push a blank vector to store the penetration vector
     * @param t a blank float to store the result of amount of penetration
     * @return result of collision detection testing
     */

    public static boolean isColliding(Form A, Form B, Vector2D VA, Vector2D VB,
            Vector2D push, FloatA t)
    {
        return collisionSat(A, B, VA, VB, push, t);
    }

    public static boolean collisionSat(Form A, Form B, Vector2D VA,
            Vector2D VB, Vector2D push, FloatA t)
    {
        // Les vecteurs VA et VB sont exprimés dans le repère world
        // Les points PA et PB sont exprimés dans le repères world
        Matrix2 OA = A.getOrientation().convertMatrix2();
        Matrix2 OB = B.getOrientation().convertMatrix2();

        Matrix2 OBi = OB.inverse();

        // La matrice orient permet le passage d'un point du repère local de A à
        // B
        Matrix2 orient = OA.multiply(OBi);
        // La matrice orientI permet le passage d'un point du repère local de B
        // à A
        Matrix2 orientI = orient.inverse();

        Vector2D PA = A.getCenter();
        Vector2D PB = B.getCenter();

        Vector2D relPos = OBi.multiply(PA.sub(PB));
        Vector2D relVel = OBi.multiply(VA.sub(VB));

        ArrayList<Vector2D> axisA = CollisionDetectorTools.getVectorsSatLocal(A, B, orient, orientI);
        ArrayList<Vector2D> axisB = CollisionDetectorTools.getVectorsSatLocal(B, A, orientI, orient);

        AxesSat axesSat = new AxesSat();

        float squaredVel = relVel.getSqMagnitude();
        if (squaredVel > 0.000001f)
        {
            if (!intervalIntersection(A, B, relVel.getPerpendicular(), relPos,
                    relVel, orientI, axesSat, t))
                return false;
        }

        for (int i = 0; i < axisA.size(); i++)
        {
            if (!intervalIntersection(A, B, orient.multiply(axisA.get(i)),
                    relPos, relVel, orientI, axesSat, t))
                return false;
        }

        for (int i = 0; i < axisB.size(); i++)
        {
            if (!intervalIntersection(A, B, axisB.get(i), relPos, relVel,
                    orientI, axesSat, t))
                return false;
        }

        CollisionDetectorTools.getPushVector(axesSat, push, t);

        // On s'assurre que les objets s'éloignent l'un de l'autre
        if (relPos.scalarProduct(push) < 0)
            push.selfMultiply(-1);

        push.set(OB.multiply(push));

        // System.out.println("Collision");
        return true;
    }

    public static boolean intervalIntersection(Form A, Form B, Vector2D axis,
            Vector2D relPos, Vector2D relVel, Matrix2 orientI, AxesSat axesSat,
            FloatA t)
    {
        axis.normalize();
        Vector2D minMaxA = A.getInterval(orientI.multiply(axis));
        Vector2D minMaxB = B.getInterval(axis);

        // On ajoute le décalage entre les deux repères
        float h = relPos.scalarProduct(axis);
        minMaxA.x += h;
        minMaxA.y += h;

        // On calcule les distances pour determiner le chevauchement
        float d0 = minMaxA.x - minMaxB.y;
        float d1 = minMaxB.x - minMaxA.y;

        if (d0 > 0 || d1 > 0)// Pas de chevauchement
        {
            float fVel = relVel.scalarProduct(axis);
            if (Math.abs(fVel) > 0.00000001f)
            {
                float t0 = -d0 / fVel;
                float t1 = d1 / fVel;

                if (t0 > t1)
                {
                    float temp = t0;
                    t0 = t1;
                    t1 = temp;
                }
                float l_tAxis = (t0 > 0) ? t0 : t1;

                if (l_tAxis < 0 || l_tAxis > t.v)
                    return false;

                axesSat.axesT.add(axis);
                axesSat.tAxesT.add(l_tAxis);
                return true;
            }
            return false;
        }
        else
        {
            axesSat.axes.add(axis);
            axesSat.tAxes.add((d0 > d1) ? d0 : d1);
            return true;
        }
    }
}

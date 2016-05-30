package org.cora.maths.collision;

import org.cora.maths.Form;
import org.cora.maths.Matrix2;
import org.cora.maths.Vector2D;

import java.util.ArrayList;


public class CollisionDetectorNoT
{
    // Collisions detection
    public static boolean isColliding(Form A, Form B)
    {
        return collisionSatFree(A, B);
    }
    
    private static boolean collisionSatFree(Form A, Form B)
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

        ArrayList<Vector2D> axisA = CollisionDetectorTools.getVectorsSatLocal(A, B, orient, orientI);
        ArrayList<Vector2D> axisB = CollisionDetectorTools.getVectorsSatLocal(B, A, orientI, orient);
        for (int i = 0; i < axisA.size(); i++)
        {
            if (!intervalIntersectionFree(A, B, orient.multiply(axisA.get(i)),
                    relPos, orientI))
                return false;
        }

        for (int i = 0; i < axisB.size(); i++)
        {
            if (!intervalIntersectionFree(A, B, axisB.get(i),
                    relPos, orientI))
                return false;
        }
        return true;
    }

    private static boolean intervalIntersectionFree(Form A, Form B, Vector2D axis,
            Vector2D relPos, Matrix2 orientI)
    {
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
            return false;
        return true;
    }
}

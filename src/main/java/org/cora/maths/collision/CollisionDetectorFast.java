package org.cora.maths.collision;

import org.cora.maths.Circle;
import org.cora.maths.FloatA;
import org.cora.maths.Vector2D;
import org.cora.maths.sRectangle;

/**
 * Created by ronan-h on 11/06/16.
 */

/**
 * Optimised collisions detection for special collisions
 */
public class CollisionDetectorFast
{
    /**
     * Detect collision
     *
     * @param r1   radius of circle 1
     * @param c1   center of circle 1
     * @param r2   radius of circle 2
     * @param c2   center of circle 2
     * @param VA   velocity of object A
     * @param VB   velocity of object B
     * @param push a blank vector to store the penetration vector
     * @param t    dt
     *
     * @return result of collision detection testing
     */
    public static boolean isColliding(float r1, Vector2D c1, float r2, Vector2D c2, Vector2D VA, Vector2D VB,
                                      Vector2D push, FloatA t)
    {
        Vector2D relPos = c1.sub(c2);
        Vector2D relVel = VA.sub(VB);
        Vector2D axisVel = relVel.clone();
        float h0 = axisVel.getPerpendicular().normalize();
        float t_axis0, t_axis1;

        // Velocity
        if (h0 > 0.00001f)
        {
            h0 = axisVel.scalarProduct(relPos);
            if ((r1 + r2) > h0)
            {
                float fVel = relVel.scalarProduct(relPos);
                if (Math.abs(fVel) > 0.00001f)
                {
                    float tAxis = Math.abs((-r1 - r2 + h0) / fVel);

                    if (tAxis < 0 || tAxis > t.v)
                        return false;

                    t_axis0 = tAxis;
                }
                return false;
            }
            else
            {
                t_axis0 = -r1 - r2 + h0;
            }
        }
        else
        {
            t_axis0 = -Float.MAX_VALUE;
        }

        float h1 = relPos.normalize();

        // RelPos
        if ((r1 + r2) > h1)
        {
            float fVel = relVel.scalarProduct(relPos) * h1;
            if (Math.abs(fVel) > 0.00001f)
            {
                float tAxis = Math.abs((-r1 - r2 + h1) / fVel);

                if (tAxis < 0 || tAxis > t.v)
                    return false;

                t_axis1 = tAxis;
            }
            return false;
        }
        else
        {
            t_axis1 = -r1 - r2 + h1;
        }

        if (t_axis0 > t_axis1)
        {
            t.v = t_axis0;
            push.set(axisVel);
        }
        else
        {
            t.v = t_axis1;
            push.set(relPos);
        }
        return true;
    }

    /**
     * Detect collision
     *
     * @param A    test circle A
     * @param B    test circle B
     * @param VA   velocity of object A
     * @param VB   velocity of object B
     * @param push a blank vector to store the penetration vector
     * @param t    a blank float to store the result of amount of penetration
     *
     * @return result of collision detection testing
     */
    public static boolean isColliding(Circle A, Circle B, Vector2D VA, Vector2D VB,
                                      Vector2D push, FloatA t)
    {
        return isColliding(A.getRadius(), A.getCenter(), B.getRadius(), B.getCenter(), VA, VB, push, t);
    }

    /**
     * @param x1   local point 0 for rec 1
     * @param y1   local point 0 for rec 1
     * @param x2   local point 0 for rec 2
     * @param y2   local point 0 for rec 2
     * @param VA   velocity of object A
     * @param VB   velocity of object B
     * @param push a blank vector to store the penetration vector
     * @param t    a blank float to store the result of amount of penetration
     *
     * @return result of collision detection testing
     */
    public static boolean isColliding(float x1, float y1,
                                      float x2, float y2,
                                      Vector2D c1, Vector2D c2,
                                      Vector2D VA, Vector2D VB,
                                      Vector2D push, FloatA t)
    {
        // Optimisation is due to same axis for the two rectangles, and no rotation

        Vector2D relPos = c1.sub(c2);
        Vector2D relVel = VA.sub(VB);
        Vector2D axisVel = relVel.clone();

        float t_axis0, t_axis1, t_axis2;
        float h;

        // test vel
        float h0 = axisVel.normalize();

        if (h0 > 0.00001f)
        {
            // How to find max? There is four points (x, y); (-x, y); (x, -y); (-x, -y)
            // (x, y) and (-x, -y) are just opposite
            // Same apply to (-x, y); (x, -y)
            // So to speed up process -> we can find the biggest value for two points
            // The biggest value is the max and -min

            float maxA = Math.max(Math.abs(x1 * axisVel.x + y1 * axisVel.y), Math.abs(x1 * axisVel.x - y1 * axisVel.y));
            float maxB = Math.max(Math.abs(x2 * axisVel.x + y2 * axisVel.y), Math.abs(x2 * axisVel.x - y2 * axisVel.y));

            // Calcul + ajout décalage
            h = axisVel.getPerpendicular().scalarProduct(relPos);

            if (Math.abs(h) > maxA + maxB)
            {
                float fVel = relVel.scalarProduct(axisVel);
                if (Math.abs(fVel) > 0.00001f)
                {
                    float l_taxis = (-maxA - maxB + Math.abs(h)) / fVel;

                    if (l_taxis < 0 || l_taxis > t.v)
                        return false;

                    t_axis0 = l_taxis;
                }
                return false;
            }
            else
            {
                t_axis0 = -maxA - maxB + Math.abs(h);
            }
        }
        else
        {
            t_axis0 = -Float.MAX_VALUE;
        }

        // Really simple, two axis, (0, 1); (1, 0)

        // Min max easy to calculate -x1, x1, -x2, x2
        h = relPos.x;
        if (Math.abs(h) < x1 + x2)
        {
            float fVel = relVel.x;
            if (Math.abs(fVel) > 0.00001f)
            {
                float l_taxis = (-x1 - x2 + Math.abs(h)) / fVel;

                if (l_taxis < 0 || l_taxis > t.v)
                    return false;

                t_axis1 = l_taxis;
            }
            return false;
        }
        else
        {
            t_axis1 = -x1 - x2 + Math.abs(h);
        }

        // Min max easy to calculate -y1, y1, -y, y2
        h = relPos.y;
        if (Math.abs(h) < y1 + y2)
        {
            float fVel = relVel.y;
            if (Math.abs(fVel) > 0.00001f)
            {
                float l_taxis = (-y1 - y2 + Math.abs(h)) / fVel;

                if (l_taxis < 0 || l_taxis > t.v)
                    return false;

                t_axis2 = l_taxis;
            }
            return false;
        }
        else
        {
            t_axis2 = -y1 - y2 + Math.abs(h);
        }

        if (t_axis0 > t_axis1)
        {
            if (t_axis0 > t_axis2)
            {
                push.set(axisVel);
                t.v = t_axis0;
            }
            else
            {
                push.set(0, 1);
                t.v = t_axis2;
            }
        }
        else
        {
            if (t_axis1 > t_axis2)
            {
                push.set(1, 0);
                t.v = t_axis1;
            }
            else
            {
                push.set(0, 1);
                t.v = t_axis2;
            }
        }

        return true;
    }

    public static boolean isColliding(sRectangle A, sRectangle B, Vector2D VA, Vector2D VB,
                                      Vector2D push, FloatA t)
    {
        return isColliding(A.getXMaxRel(), A.getYMaxRel(), B.getXMaxRel(), B.getYMaxRel(),
                A.getC1(), B.getC1(), VA, VB, push, t);
    }

}
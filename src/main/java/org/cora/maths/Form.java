package org.cora.maths;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for polygon
 */
public class Form implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    // update
    protected float omega, scale;
    protected boolean flipH, flipV;
    protected Matrix3 orientation;

    protected ArrayList<Vector2D> points;
    protected float radius;

    protected float xMin, xMax, yMin, yMax;
    protected ArrayList<Vector2D> savedVectorsLocal;

    /**
     * @param size number of points
     */
    public Form(int size)
    {
        points = new ArrayList<Vector2D>(size);
        for (int i = 0; i < size; i++)
        {
            Vector2D p = new Vector2D();
            points.add(p);
        }

        omega = 0;
        scale = 1f;
        flipH = false;
        flipV = false;
        radius = 0;

        orientation = Matrix3.orientation(omega, scale, flipH, flipV,
                new Vector2D());
    }

    public Form()
    {
        points = new ArrayList<Vector2D>();

        omega = 0;
        scale = 1f;
        flipH = false;
        flipV = false;

        orientation = Matrix3.orientation(omega, scale, flipH, flipV,
                new Vector2D());
    }

    /**
     * Create copy of a form
     *
     * @param form pattern
     */
    public Form(Form form)
    {

        points = new ArrayList<Vector2D>(form.size());

        for (int i = 0; i < form.size(); i++)
        {
            points.add((Vector2D) form.getLocal(i).clone());
        }

        this.omega = form.getOmega();
        this.scale = form.getScale();
        this.flipH = form.getFlipH();
        this.flipV = form.getFlipV();
        this.radius = form.getOriginalRadius();

        this.xMin = form.getXMinRel();
        this.xMax = form.getXMaxRel();
        this.yMin = form.getYMinRel();
        this.yMax = form.getYMaxRel();

        orientation = Matrix3.orientation(omega, scale, flipH, flipV,
                form.getCenter());
    }

    @Override
    public Form clone()
    {
        Form form = new Form(this);
        return form;
    }

    /**
     *
     * @return scale with scaling
     */
    public float getRadius()
    {
        return radius*scale;
    }

    /**
     *
     * @return scale without scaling
     */
    public float getOriginalRadius()
    {
        return radius;
    }

    /**
     * Clear all transformations
     */
    public void clearTransformations()
    {
        omega = 0;
        scale = 1f;
        flipH = false;
        flipV = false;

        orientation.setIdentity();
    }

    /**
     * Copy transformations of one form
     */
    public void setInit(Form form)
    {
        this.omega = form.getOmega();
        this.scale = form.getScale();
        this.flipH = form.getFlipH();
        this.flipV = form.getFlipV();

        updateOrientation();
    }

    public Matrix3 getOrientation()
    {
        return orientation;
    }

    public Matrix3 getOrientationInverse()
    {
        return orientation.inverse();
    }

    public float getOmega()
    {
        return omega;
    }

    public float getScale()
    {
        return scale;
    }

    public boolean getFlipH()
    {
        return flipH;
    }

    public boolean getFlipV()
    {
        return flipV;
    }

    /**
     * @return center of the polygon in world coordinates
     */
    public Vector2D getCentroidWorld()
    {
        Vector2D center = this.getCentroidLocal();
        Vector2D centerW = orientation.multiply(center);
        return centerW;
    }

    /**
     * @return center of the polygon in local coordinates
     */
    public Vector2D getCentroidLocal()
    {
        if (points.size() == 0)
            return new Vector2D();
        else if (points.size() == 1)
            return this.getLocal();
        else if (points.size() == 2)
        {
            ArrayList<Vector2D> points = this.getPointsLocal();
            return new Vector2D((points.get(0).x + points.get(1).x) / 2,
                    (points.get(0).y + points.get(1).y) / 2);
        }
        Vector2D center = new Vector2D();
        float x0 = 0, y0 = 0, x1 = 0, y1 = 0, signedArea = 0, a = 0;

        ArrayList<Vector2D> points = this.getPointsLocal();
        for (int j = points.size() - 1, i = 0; i < points.size(); j = i, i++)
        {
            x0 = points.get(i).x;
            y0 = points.get(i).y;
            x1 = points.get(j).x;
            y1 = points.get(j).y;
            a = x0 * y1 - x1 * y0;
            signedArea += a;
            center.x += (x0 + x1) * a;
            center.y += (y0 + y1) * a;
        }

        signedArea *= 0.5f;
        center.x /= (6f * signedArea);
        center.y /= (6f * signedArea);

        return center;
    }

    /**
     * Compute the center of this form
     */
    public void updateCenter()
    {
        Vector2D newCenter = this.getCentroidWorld();

        Matrix3 lastCoor = this.orientation; // this.center
        Matrix3 newCoor = this.orientation.clone();
        newCoor.setPos(newCenter);
        Matrix3 inverse = newCoor.inverse();
        Matrix3 result = inverse.multiply(lastCoor);

        radius = 0;

        for (int i = 0; i < points.size(); i++)
        {
            /*
             * result équivaut à ces opérations
             * 
             * Vector2D p = points.get(i); Vector2D pW =
             * orientation.multiply(p); Vector2D pN = inverse.multiply(pW);
             */
            points.get(i).set(result.multiply(points.get(i)));

            radius = Math.max(points.get(i).getSqMagnitude(), radius);
        }

        radius = (float) Math.sqrt(radius);
        orientation.setPos(newCenter);

        updateVectorsLocal();
    }

    /**
     * Update local vectors
     */
    public void updateVectorsLocal()
    {
        savedVectorsLocal.clear();

        for (int j = size() - 1, i = 0; i < size(); j = i, i++)
        {
            Vector2D v = new Vector2D(points.get(j), points.get(i));
            savedVectorsLocal.add(v.getPerpendicular());
        }

        // On enleve les vectors colinéaires
        for (int i = 0; i < savedVectorsLocal.size() - 1; i++)
        {
            // Si on est en dessous de 2 vecteurs ca sert à rien de continuer,
            // On sait que ces 2 vecteurs (ou moins) ne sont pas colinéaires
            if (savedVectorsLocal.size() < 3)
                break;

            for (int j = i + 1; j < savedVectorsLocal.size(); j++)
            {
                if (savedVectorsLocal.get(i).isColinear(savedVectorsLocal.get(j)))
                {
                    savedVectorsLocal.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    /**
     * Update min max x, y
     */
    public void updateBound()
    {
        Vector2D v = points.get(0).getRotatedRadians(omega);

        xMin = yMin = v.x;
        xMax = yMax = v.y;

        for (int i = 1; i < points.size(); i++)
        {
            v = points.get(i).getRotatedRadians(omega);
            if (v.x < xMin)
            {
                xMin = v.x;
            }
            else if (v.x > xMax)
            {
                xMax = v.x;
            }

            if (v.y < yMin)
            {
                yMin = v.y;
            }
            else if (v.y > yMax)
            {
                yMax = v.y;
            }
        }

        xMin *= scale * ((flipV) ? 1 : -1);
        xMax *= scale * ((flipV) ? 1 : -1);

        if (flipV)
        {
            float temp = xMin;
            xMin = - xMax * scale;
            xMax = - temp * scale;
        }
        else
        {
            xMin *= scale;
            xMax *= scale;
        }

        if (flipH)
        {
            float temp = yMin;
            yMin = - yMax * scale;
            yMax = - temp * scale;
        }
        else
        {
            yMin *= scale;
            yMax *= scale;
        }
    }

    /**
     * Get saved edge vector in local coordinates
     * @return list of vec
     */
    public ArrayList<Vector2D> getSavedVectorsLocal()
    {
        return savedVectorsLocal;
    }

    /**
     * Call update Bound first
     * Get saved x Min in a relative coordinate
     * With scale transformation
     * @return xMin
     */
    public float getXMinRel()
    {
        return xMin;
    }

    /**
     * Call update Bound first
     * Get saved x Max in a relative coordinate
     * With scale transformation
     * @return xMax
     */
    public float getXMaxRel()
    {
        return xMax;
    }

    /**
     * Call update Bound first
     * Get saved y Min in a relative coordinate
     * With scale transformation
     * @return xMin
     */
    public float getYMinRel()
    {
        return yMin;
    }

    /**
     * Call update Bound first
     * Get saved y Max in a relative coordinate
     * With scale transformation
     * @return yMax
     */
    public float getYMaxRel()
    {
        return yMax;
    }


    /**
     * return min and max projection of the polygon on the vector axis
     *
     * @param axis projection axis
     *
     * @return MinMax
     */
    public Vector2D getInterval(Vector2D axis)
    {
        Vector2D minMax = new Vector2D();

        minMax.x = minMax.y = points.get(0).scalarProduct(axis);
        for (int i = 1; i < points.size(); i++)
        {
            float scalar = points.get(i).scalarProduct(axis);
            if (scalar < minMax.x)
            {
                minMax.x = scalar;
            }
            else if (scalar > minMax.y)
            {
                minMax.y = scalar;
            }
        }
        return minMax;
    }

    public void updateOrientation()
    {
        orientation.setOrientation(omega, scale, flipH, flipV);
    }

    /**
     * Translate the polygon form actualCenter to the desired center
     *
     * @param center
     */
    public void setCenter(Vector2D center)
    {
        Vector2D vec = new Vector2D(this.getCenter(), center);
        this.translate(vec);
    }

    /**
     * Move the point to the desired location. You will need to recall endForm() to update information.
     *
     * @param n
     * @param p
     */
    public void setPoint(int n, Vector2D p)
    {
        assert (n < points.size());
        points.set(n, orientation.inverse().multiply(p));
    }

    public void addPoint(Vector2D p)
    {
        // Pas d'actualisation du centre
        if (orientation.getDeterminant() != 0f)
            points.add(orientation.inverse().multiply(p));
        else
            points.add(new Vector2D(this.getCenter(), p));
    }

    public Vector2D removePoint(int i)
    {
        return points.remove(i);
    }

    public void removeLast()
    {
        this.removePoint(points.size() - 1);
    }

    public int size()
    {
        return points.size();
    }

    // getter
    public float getLocalX()
    {
        return points.get(0).x;
    }

    public float getLocalY()
    {
        return points.get(0).y;
    }

    public Vector2D getLocal()
    {
        return points.get(0);
    }

    public float getLocalX(int n)
    {
        return points.get(n).x;
    }

    public float getLocalY(int n)
    {
        return points.get(n).y;
    }

    public Vector2D getLocal(int n)
    {
        return points.get(n);
    }

    public float getX()
    {
        return this.getCenterX();
    }

    public float getY()
    {
        return this.getCenterY();
    }

    /**
     * @param n the nrd point
     *
     * @return the x coordinate
     */
    public float getX(int n)
    {
        assert (n < points.size());
        Vector2D local = points.get(n);
        float x = orientation.multiplyX(local);
        return x;
    }

    /**
     * @param n the nrd point
     *
     * @return the y coordinate
     */
    public float getY(int n)
    {
        assert (n < points.size());
        Vector2D local = points.get(n);
        float y = orientation.multiplyY(local);
        return y;
    }

    public Vector2D get(int n)
    {
        assert (n < points.size());
        // change local to world
        Vector2D local = points.get(n);
        Vector2D world = orientation.multiply(local);
        return world;
    }

    public float getCenterX()
    {
        return orientation.getX();
    }

    public float getCenterY()
    {
        return orientation.getY();
    }

    public Vector2D getCenter()
    {
        return orientation.getPos();
    }

    public int[] getXIntArray()
    {
        int[] l_X = new int[points.size()];
        for (int i = 0; i < points.size(); i++)
        {
            l_X[i] = (int) this.getX(i);
        }
        return l_X;
    }

    public int[] getYIntArray()
    {
        int[] l_Y = new int[points.size()];
        for (int i = 0; i < points.size(); i++)
        {
            l_Y[i] = (int) this.getY(i);
        }
        return l_Y;
    }

    public float getMinX()
    {
        float xMin = getX(0);
        for (int i = 1; i < points.size(); i++)
        {
            float x = getX(i);
            if (x < xMin)
                xMin = x;
        }
        return xMin;
    }

    public float getMinY()
    {
        float yMin = getY(0);
        for (int i = 1; i < points.size(); i++)
        {
            float y = getY(i);
            if (y < yMin)
                yMin = y;
        }
        return yMin;
    }

    public float getMaxX()
    {
        float xMax = getX(0);
        for (int i = 1; i < points.size(); i++)
        {
            float x = getX(i);
            if (x > xMax)
                xMax = x;
        }
        return xMax;
    }

    public float getMaxY()
    {
        float yMax = getY(0);
        for (int i = 1; i < points.size(); i++)
        {
            float y = getY(i);
            if (y > yMax)
                yMax = y;
        }
        return yMax;
    }

    /**
     * @return xmin, xmax, ymin, ymax
     */
    public float[] getMinMax()
    {
        float minMax[] = new float[4];

        if (size() < 1)
            return minMax;

        minMax[0] = minMax[1] = getX(0);
        minMax[2] = minMax[3] = getY(0);

        for (int i = 1; i < points.size(); i++)
        {
            float x = getX(i);
            float y = getY(i);

            if (x < minMax[0])
            {
                minMax[0] = x;
            }
            else if (x > minMax[1])
            {
                minMax[1] = x;
            }

            if (y < minMax[2])
            {
                minMax[2] = y;
            }
            else if (y > minMax[3])
            {
                minMax[3] = y;
            }
        }
        return minMax;
    }

    /**
     * Get rectangle with no rotation containing the form
     * @return created sRectangle
     */
    public sRectangle getSRectangleBound()
    {
        float minMax[] = getMinMax();
        return new sRectangle(minMax[0],
                minMax[2],
                minMax[1] - minMax[0],
                minMax[3] - minMax[2]);
    }

    /**
     * Get circle containing the form
     * @return created sRectangle
     */
    public Circle getCircleBound()
    {
        float radius = 0;
        float dist;
        for (int i = 0; i < size(); i++)
        {
            dist = points.get(i).getSqMagnitude();

            if (radius < dist)
            {
                radius = dist;
            }
        }
        return new Circle(getCenter(), (float) (Math.sqrt(radius)*getScale()));
    }

    public ArrayList<Vector2D> getVectorsLocal()
    {
        ArrayList<Vector2D> l_vectors = new ArrayList<Vector2D>(points.size());
        for (int j = points.size() - 1, i = 0; i < points.size(); j = i, i++)
        {
            l_vectors.add(i, new Vector2D(points.get(j), points.get(i)));
        }
        return l_vectors;
    }

    public ArrayList<Vector2D> getVectorsWorld()
    {
        ArrayList<Vector2D> l_vectors = getVectorsLocal();
        for (int i = 0; i < l_vectors.size(); i++)
        {
            l_vectors.get(i).set(orientation.multiply(l_vectors.get(i)));
        }
        return l_vectors;
    }

    public ArrayList<Vector2D> getPointsLocal()
    {
        return points;
    }

    public ArrayList<Vector2D> getPointsWorld()
    {
        if (orientation.getX() == 0 && orientation.getY() == 0 && omega == 0
                && scale == 1f && !flipV && !flipH)
            return this.getPointsLocal();

        ArrayList<Vector2D> pointsW = new ArrayList<Vector2D>(points.size());
        for (int i = 0; i < points.size(); i++)
        {
            pointsW.add(this.get(i));
        }
        return pointsW;
    }

    public Vector2D transformLocalToWorld(Vector2D point)
    {
        return orientation.multiply(point);
    }

    public Vector2D transformWorldToLocal(Vector2D point)
    {
        Matrix3 inverse = orientation.inverse();
        return inverse.multiply(point);
    }

    // Transformations
    public void translate(Vector2D v)
    {
        orientation.translate(v);
    }

    public void translateX(float x)
    {
        orientation.translateX(x);
    }

    public void translateY(float y)
    {
        orientation.translateY(y);
    }

    public void rotateDegrees(float omega, Vector2D center)
    {
        rotateRadians((float) (omega * Math.PI) / 180, center);
    }

    public void rotateDegrees(float omega)
    {
        rotateRadians((float) (omega * Math.PI) / 180);
    }

    public void rotateRadians(float omega, Vector2D center)
    {
        this.omega += omega;
        orientation.rotateRadiansFree(omega, center);
        updateOrientation();
    }

    public void rotateRadians(float omega)
    {
        this.omega += omega;
        updateOrientation();
    }

    public void scale(float factor, Vector2D center)
    {
        scale *= factor;
        orientation.scale(factor, center);
    }

    public void scale(float factor)
    {
        scale *= factor;
        orientation.scale(factor);
    }

    public void flipH(Vector2D center)
    {
        this.flipH = !this.flipH;
        orientation.flipH(center);
    }

    public void flipH()
    {
        this.flipH = !this.flipH;
        orientation.flipH();
    }

    public void flipV(Vector2D center)
    {
        this.flipV = !this.flipV;
        orientation.flipV(center);
    }

    public void flipV()
    {
        this.flipV = !this.flipV;
        orientation.flipV();
    }

    public void setPos(Vector2D v)
    {
        orientation.setPos(v);
    }

    public void setX(float x)
    {
        orientation.setX(x);
    }

    public void setY(float y)
    {
        orientation.setY(y);
    }

    public void setRadians(float omega)
    {
        float d = omega - this.omega;
        this.omega = omega;
        orientation.rotateRadiansFree(d, new Vector2D());
        updateOrientation();
    }

    public void setScale(float scale)
    {
        float factor = scale / this.scale;
        this.scale = scale;
        orientation.scale(factor);
    }

    public void setScale(float scale, Vector2D center)
    {
        float factor = scale / this.scale;
        this.scale = scale;
        orientation.scale(factor, center);
    }

    public void setFlipH(boolean flipH)
    {
        if (this.flipH != flipH)
        {
            this.flipH = flipH;
            orientation.flipH();
        }
    }

    public void setFlipV(boolean flipV)
    {
        if (this.flipV != flipV)
        {
            this.flipV = flipV;
            orientation.flipV();
        }
    }

    // Mass Inertia
    public float calculateMass(float density)
    {
        if (points.size() == 0)
            return 0;
        else if (points.size() < 3)
            return 5.0f * density;

        float mass = 0f;

        for (int j = points.size() - 1, i = 0; i < points.size(); j = i, i++)
        {
            Vector2D P0 = points.get(j);
            Vector2D P1 = points.get(i);
            mass += (float) Math.abs(P0.crossProductZ(P1));
        }

        mass *= density * 0.5f;

        return mass;
    }

    public float calculateInertia(float inverseMass)
    {
        if (points.size() < 2)
            return 0;

        float denom = 0.0f;
        float numer = 0.0f;

        for (int j = points.size() - 1, i = 0; i < points.size(); j = i, i++)
        {
            Vector2D P0 = points.get(j);
            Vector2D P1 = points.get(i);

            float a = (float) Math.abs(P0.crossProductZ(P1));
            float b = (P0.getSqMagnitude() + P0.scalarProduct(P1) + P1
                    .getSqMagnitude());

            denom += (a * b);
            numer += a;
        }

        return inverseMass / ((1 / 6f) * (denom / numer));
    }

    /**
     * @return surface covered by the polygon
     */
    public float calculateSurface()
    {
        float surface = 0;
        if (points.size() < 3)
            return surface;
        for (int i = points.size() - 2, i1 = points.size() - 1, i2 = 0; i2 < points
                .size(); i = i1, i1 = i2, i2++)
            surface += points.get(i1).x * (points.get(i2).y - points.get(i).y)
                    + points.get(i1).y * (points.get(i).x - points.get(i2).x);
        surface /= 2;
        return surface;
    }

    // Convex

    /**
     * Test if a polygon is convex
     *
     * @return result
     */
    public boolean isConvex()
    {
        if (points.size() < 3)
            return true;

        ArrayList<Vector2D> vectors = this.getVectorsLocal();
        int i = 0;
        float crossProductZ = 0;

        while (crossProductZ == 0 && i < vectors.size())
        {
            crossProductZ = vectors.get(i).crossProductZ(
                    vectors.get((i + 1) % vectors.size()));
            i++;
        }

        for (; i < vectors.size(); i++)
        {
            float crossProductZ2 = vectors.get(i).crossProductZ(
                    vectors.get((i + 1) % vectors.size()));

            // Si les deux vectoriels z sont de sens différents, alors la forme
            // n'est pas convexe
            if (crossProductZ * crossProductZ2 < 0)
                return false;
        }

        return true;
    }

    public int getClockwise()
    {
        if (points.size() < 3)
            return 0;
        double sum = 0;
        // float sumInt = 0;
        for (int i = 0; i < points.size(); i++)
        {
            sum = sum
                    + (Math.PI - points.get((i + 1) % points.size()).getAngle(
                    points.get(i), points.get((i + 2) % points.size())));
        }
        if (Math.PI * 2 - 0.001 < Math.abs(sum)
                && Math.abs(sum) < 2 * Math.PI + 0.001)
        {
            if (sum < 0)
                return -1;
            return 1;
        }
        else
        {
            // Si des cotés se croisent, ce n'est pas un polygone regulier
            System.out.println("bad: " + sum);
            return 0;
        }
    }

    /**
     * @return all edges of form in local coordinates
     */
    public ArrayList<Edge> getEdgesLocal()
    {
        int factor = this.getClockwise();
        ArrayList<Edge> edges = new ArrayList<Edge>();
        if (points.size() < 2 || factor == 0)
            return edges;

        ArrayList<PointType> pointsType = new ArrayList<PointType>();

        if (factor == 1)
        {
            for (int i = points.size() - 1; i > -1; i--)
            {
                PointType p = new PointType(points.get(i));
                p.posPoint = i;
                p.posEdge = pointsType.size();
                p.type = -1;
                pointsType.add(p);
            }
        }
        else
        {
            for (int i = 0; i < points.size(); i++)
            {
                PointType p = new PointType(points.get(i));
                p.posPoint = i;
                p.posEdge = pointsType.size();
                p.type = -1;
                pointsType.add(p);
            }
        }

        for (int i = 0; i < points.size(); i++)
        {
            Edge edge = new Edge();

            edge.p0 = pointsType.get(i);
            edge.p1 = pointsType.get((i + 1) % pointsType.size());

            if (i != 0)
            {
                edge.prev = edges.get(edges.size() - 1);
                edges.get(edges.size() - 1).next = edge;
            }
            edges.add(edge);
        }
        edges.get(0).prev = edges.get(points.size() - 1);
        edges.get(points.size() - 1).next = edges.get(0);

        return edges;
    }
}

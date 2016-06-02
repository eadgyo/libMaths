package org.cora.maths;


public class Circle extends RoundForm
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private float radius;

    public Circle()
    {
        super();
        radius = 0;
    }

    public Circle(Vector2D center, float radius)
    {
        super();
        this.radius = radius;
        orientation.setPos(center);
    }

    public Circle(float radius)
    {
        super();
        this.radius = radius;
    }

    public Circle(Circle circle)
    {
        super();
        this.radius = circle.getRadius();
        orientation.setPos(circle.getCenter());
    }

    @Override
    public Object clone()
    {
        return new Circle(this);
    }

    @Override
    public void scale(float factor, Vector2D center)
    {
        super.scale(factor, center);
        this.radius *= factor;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    public float getRadius()
    {
        return radius;
    }

    public float getMinX()
    {
        return getCenterX() - radius;
    }

    public float getMinY()
    {
        return getCenterY() - radius;
    }

    public float getMaxX()
    {
        return getCenterX() + radius;
    }

    public float getMaxY()
    {
        return getCenterY() + radius;
    }

    /**
     * @return xmin, xmax, ymin, ymax
     */
    public float[] getMinMax()
    {
        float minMax[] = new float[4];
        minMax[0] = getMinX();
        minMax[1] = getMaxX();
        minMax[2] = getMinY();
        minMax[3] = getMaxY();
        return minMax;
    }

    /**
     * Get circle containing the form
     * @return created sRectangle
     */
    public Circle getCircleBound()
    {
        return this;
    }

    @Override
    public Vector2D getInterval(Vector2D axis)
    {
        Vector2D minMax = new Vector2D();
        minMax.x = -radius;
        minMax.y = radius;
        return minMax;
    }

    @Override
    public float calculateInertia(float inverseMass)
    {
        return inverseMass / (float) (Math.PI * Math.pow(radius, 4) / 2);
    }

    public boolean isColliding(Circle circle)
    {
        Vector2D vec = new Vector2D(circle.getCenter(), this.getCenter());
        if (vec.getMagnitude() <= this.radius + circle.radius)
            return true;
        return false;
    }
}

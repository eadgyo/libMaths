package org.cora.maths;

public class PointType extends Vector2D
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PointType(Vector2D point2d)
    {
	super(point2d);
    }

    public static final int REGULAR = 0;
    public static final int START = 1;
    public static final int END = 2;
    public static final int SPLIT = 3;
    public static final int MERGE = 4;

    int posPoint;
    int posEdge;
    int type;
}

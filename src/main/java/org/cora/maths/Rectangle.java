package org.cora.maths;


public class Rectangle extends Form
{
	public final static long serialVersionUID = -1976089904607398674L;
	
	protected Vector2D length;
	
	public Rectangle()
	{
		super(4);
		length = new Vector2D(0);
		set(new Vector2D(0, 0), new Vector2D(0, 0), 0);
	}
	public Rectangle(Rectangle rec)
	{
		super(rec);
		length = new Vector2D();
		length.set(rec.getLength());
	}
	public Rectangle(Form form)
	{
		super(form);
		assert(form.size() == 4);
		
		//Les 2 points doivent être opposés
		Vector2D vec = new Vector2D(points.get(0), points.get(2));
		orientation.setPos(points.get(0).add(vec.multiply(0.5f)));
		
		Vector2D side = new Vector2D(points.get(0), points.get(1));
		length = new Vector2D();
		length.x = side.getMagnitude();
		
		side.set(points.get(0), points.get(1));
		length.y = side.getMagnitude();
	}

	@Override
	public Rectangle clone()
	{
		return new Rectangle(this);
	}

	/**
	 * @param center center of the rectangle
	 * @param length rec size
	 * @param omega angle of the rectangle
     */
	public Rectangle(Vector2D center, Vector2D length, float omega)
	{
		super(4);
		this.length = new Vector2D();
		set(center, length, omega);
	}

    @Override
    public void updateVectorsLocal()
    {
        savedVectorsLocal.clear();

        for (int i = 0; i < size(); i++)
        {
            savedVectorsLocal.add(new Vector2D(points.get(i+1), points.get(i)).getPerpendicular());
        }
    }

	/**
	 * @param x center of the rectangle
     * @param y center of the rectangle
	 * @param width rec size
     * @param height rec size
	 * @param omega angle of the rectangle
	 */
	public Rectangle(float x, float y, float width, float height, float omega)
	{
		super(4);
		this.length = new Vector2D();
		set(x, y, width, height, omega);
	}

    /**
     * @param center center of the rectangle
     * @param length rec size
     */
    public Rectangle(Vector2D center, Vector2D length)
    {
        super(4);
        this.length = new Vector2D();
        set(center, length, omega);
    }

    /**
     * @param x center of the rectangle
     * @param y center of the rectangle
     * @param width rec size
     * @param height rec size
     */
    public Rectangle(float x, float y, float width, float height)
    {
        super(4);
        this.length = new Vector2D();
        set(x, y, width, height);
    }

    /**
     * @param center center of the rectangle
     * @param length rec size
     * @param omega angle of the rectangle
     */
	public void set(Vector2D center, Vector2D length, float omega)
	{
		this.clearTransformations();
		orientation.setPos(center);
		this.length.set(length);
		
		points.get(0).set(- 0.5f*length.x,- 0.5f*length.y);
		points.get(1).set(- 0.5f*length.x,+ 0.5f*length.y);
		points.get(2).set(+ 0.5f*length.x,+ 0.5f*length.y);
		points.get(3).set(+ 0.5f*length.x,- 0.5f*length.y);
		
		this.rotateRadians(omega, center);
	}

    /**
     * @param x center of the rectangle
     * @param y center of the rectangle
     * @param width rec size
     * @param height rec size
     * @param omega angle of the rectangle
     */
    public void set(float x, float y, float width, float height, float omega)
    {
        set(new Vector2D(x, y), new Vector2D(width, height), omega);
    }

    /**
     * @param center center of the rectangle
     * @param length rec size
     */
    public void set(Vector2D center, Vector2D length)
    {
        this.clearTransformations();
        orientation.setPos(center);
        this.length.set(length);

        points.get(0).set(- 0.5f*length.x,- 0.5f*length.y);
        points.get(1).set(- 0.5f*length.x,+ 0.5f*length.y);
        points.get(2).set(+ 0.5f*length.x,+ 0.5f*length.y);
        points.get(3).set(+ 0.5f*length.x,- 0.5f*length.y);
    }

    /**
     * @param x center of the rectangle
     * @param y center of the rectangle
     * @param width rec size
     * @param height rec size
     */
    public void set(float x, float y, float width, float height)
    {
        set(new Vector2D(x, y), new Vector2D(width, height));
    }

    /**
     * @param width rec size
     * @param height rec size
     */
    public void setLength(float width, float height)
    {
        this.length.set(width, height);

        points.get(0).set(- 0.5f*length.x,- 0.5f*length.y);
        points.get(1).set(- 0.5f*length.x,+ 0.5f*length.y);
        points.get(2).set(+ 0.5f*length.x,+ 0.5f*length.y);
        points.get(3).set(+ 0.5f*length.x,- 0.5f*length.y);
    }

    /**
     * @param length rec size
     */
    public void setLength(Vector2D length)
    {
        this.length.set(length);

        points.get(0).set(- 0.5f*length.x,- 0.5f*length.y);
        points.get(1).set(- 0.5f*length.x,+ 0.5f*length.y);
        points.get(2).set(+ 0.5f*length.x,+ 0.5f*length.y);
        points.get(3).set(+ 0.5f*length.x,- 0.5f*length.y);
    }


	public void set(Rectangle rec)
	{
		this.set(rec.getCenter(), rec.getLength(), rec.getAngle());
		this.setInit(rec);
	}
	public void set(Form form)
	{
		assert(form.size() == 4);
		
		for(int i=0; i<4; i++)
			points.set(i, form.getLocal(i));
		
		//Les 2 points doivent être opposés
		Vector2D vec = new Vector2D(form.getLocal(0), form.getLocal(2));
		orientation.setPos(form.getLocal(0).add(vec.multiply(0.5f)));
		
		Vector2D side = new Vector2D(form.getLocal(0), form.getLocal(1));
		length.x = side.getMagnitude();
		
		side.set(form.getLocal(0), form.getLocal(3));
		length.y = side.getMagnitude();
		
		super.setInit(form);
	}
	public void setLeftX(float x)
	{
	    translateX(x - this.getCenterX());
	}
	public void setLeftY(float y)
    {
        translateY(y - this.getCenterY());
    }
	public void setLeft(float x, float y)
	{
	    setLeftX(x);
	    setLeftY(y);
	}
	public void setLeft(Vector2D left)
	{
	    setLeft(left.x, left.y);
	}
	
	/*public void setLeft(Point2D left, Point2D length, float omega)
	{
		set(center, length, omega);
		Point2D vec = new Point2D(this.getLeft(), left);
		this.translate(vec);
	}*/
	
	public Vector2D getVecLocal()
	{
		return new Vector2D(this.points.get(0), this.points.get(3));
	}
	public Vector2D getVecWorld()
	{
		Vector2D vec = new Vector2D(this.points.get(0), this.points.get(3));
		return orientation.multiply(vec);
	}
	public Vector2D getLeft()
	{
		return this.get(0);
	}
	public float getLeftX()
	{
	    return this.getX(0);
	}
	public float getLeftY()
	{
	    return this.getY(0);
	}
	public Vector2D getLength()
	{
		return length;
	}
	public float getWidth()
	{
		return length.x;
	}
	public float getHeight()
	{
		return length.y;
	}
	
	@Override
	public void rotateDegrees(float omega, Vector2D center)
	{
		//omega = Extend.modulo(omega, 360f);
		this.rotateRadians((float) (omega*Math.PI)/180, center);
	}
	@Override
	public void rotateRadians(float omega, Vector2D center)
	{
		super.rotateRadians(omega, center);
	}
	public float getAngle(Vector2D vec)
	{
		Vector2D l_vec1 = this.getVecWorld();
		if(l_vec1.x == 0 && l_vec1.y == 0)
			return this.getAngle();
		return l_vec1.getAngle(vec);
	}
	public float getAngle()
	{
		return this.omega;
	}
	
	public void scale(float factor)
	{
	    super.scale(factor);
	    length.selfMultiply(factor);
	}
	
	public void scale(float factor, Vector2D center)
	{
		super.scale(factor, center);
		length.selfMultiply(factor);
	}
}

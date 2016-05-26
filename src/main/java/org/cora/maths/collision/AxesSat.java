package org.cora.maths.collision;

import java.util.ArrayList;

import org.cora.maths.Vector2D;


public class AxesSat
{
    // premier point, les autres == vectors
    // On conserve les axis et la distance
    public ArrayList<Vector2D> axes   = new ArrayList<Vector2D>();
    public ArrayList<Float>    tAxes  = new ArrayList<Float>();

    // Si la collision est future on conserve le temps avant la collision et
    // l'axe
    public ArrayList<Vector2D> axesT  = new ArrayList<Vector2D>();
    public ArrayList<Float>    tAxesT = new ArrayList<Float>();
}

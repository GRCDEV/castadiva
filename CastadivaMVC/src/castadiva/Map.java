/*
 * Map.java
 *
 * Created on 10 de noviembre de 2006, 10:56
 *
 *
 */

package castadiva;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Map extends JPanel {
    private int tagMetres;
    //private APs numberDrawingAP;
    private CastadivaModel m_model;
    private boolean graphRange;
    private float edgex;
    private float edgey;
    private boolean labels = true;
    private final Integer  NUM_SUBLINES=11;
    private boolean directions = false;
    private boolean grid = true;
    

    
    /**
     * Creates a new instance of Map
     */
    public Map(CastadivaModel model, boolean visualization, boolean tmp_grid, float limitex, float limitey) {
        m_model = model;
        graphRange = visualization;
        edgex = limitex;
        edgey = limitey;
        grid = tmp_grid;
    }
    
    public Map(CastadivaModel model, float limitex, float limitey) {
        m_model = model;
        graphRange = false;
        labels = false;
        edgex = limitex;
        edgey = limitey;
    }
    
    public void MapChange(boolean visualizacion, boolean etiquetas, boolean tmp_grid, Float limitex, 
            Float limitey, boolean speed){
        graphRange = visualizacion;
        labels = etiquetas;
        edgex = limitex;
        edgey = limitey;
        directions = speed;
        grid = tmp_grid;
    }
    
    private void GraphDrawing(Graphics g, int numNodo, int xp1, int yp1){
        float x2;
        float y2;
        
        
        for(int j=0;j<m_model.HowManyAP();j++){
            if(m_model.WhatVisibilityMatrix()[numNodo][j] > 0 ){
                x2 = m_model.GetAP(j).x;
                y2 = m_model.GetAP(j).y;
                //Pasmos a pixeles la posición,
                int xp2 = (int)(x2 * this.getWidth() / m_model.WhatBoardSize());
                int yp2 = (int)(y2 * this.getHeight() / m_model.WhatBoardSize());
                if(m_model.WhatVisibilityMatrix()[j][numNodo] > 0 )g.drawLine(xp1, yp1, (int)xp2, (int)yp2);
                else{
                    //Draw non continuos line.
                    Float xCoordeantesDifference = xp2-(float)xp1;
                    Float yCoordeantesDifference = yp2-(float)yp1;
                    Float xIncrease = xCoordeantesDifference/NUM_SUBLINES;
                    Float yIncrease = yCoordeantesDifference/NUM_SUBLINES;
                    for(int p=0; p<=NUM_SUBLINES; p+=2) g.drawLine((int)(xp1+xIncrease*p), (int)(yp1+yIncrease*p), (int)(xp1+xIncrease*(p+1)), (int)(yp1+yIncrease*(p+1)));
                }
            }
        }
    }
    
    private void APDrawing(Graphics g){
        //AP APD;
        float x;
        float y;
        float range;
        String name;
                
        for(int k=0; k<m_model.HowManyAP(); k++) {
            //APD = numberDrawingAP.Get(k);
            x = m_model.GetAP(k).x;
            y = m_model.GetAP(k).y;
            range = m_model.GetAP(k).range;
            name = m_model.GetAP(k).WhatAP();
            
            //Pasamos a pixeles la posicion,
            int xp = (int)(x * this.getWidth() / m_model.WhatBoardSize());
            int yp = (int)(y * this.getHeight() / m_model.WhatBoardSize());
            //Lo mismo para el range de la señal.
            int axp = (int)(range * this.getWidth() / m_model.WhatBoardSize());
            int ayp = (int)(range * this.getHeight() / m_model.WhatBoardSize());
            
            //Seleccionamos un color de acuerdo unas reglas.
            int re = 0 + k * 127;
            if (re > 224 ) re = re % 225;
            int gr = 0 + k * 17;
            if (gr > 224 ) gr = gr % 225;
            int bl = 255 - k * 73;
            if (bl < 0 ) bl = 255 + bl % 255;
            g.setColor(new java.awt.Color(re, gr, bl));
            
            //Dibujamos el AP y su range.
            if (labels) g.drawString(name, xp+4, yp-2);
            g.fillRect(xp-2, yp-2, 5, 5);
            if(graphRange){
                if(m_model.GetAP(k).showRange)
                g.drawArc(xp-axp,yp-ayp,axp*2,ayp*2,0,360);
            }else{
                GraphDrawing(g,k, xp, yp);
            }
            //Draw the movement of the nodes.
            if(directions){
                if(m_model.simulationSeconds < m_model.GetSimulationTime() && m_model.simulationSeconds > 0){
                    try{
                   NodeCheckPoint nextPoint = m_model.nodePositions[k][m_model.simulationSeconds + 1];
                   int nextXp = (int)(nextPoint.xCoordinate * this.getWidth() / m_model.WhatBoardSize());
                   int nextYp = (int)(nextPoint.yCoordinate * this.getHeight() / m_model.WhatBoardSize());
                   g.drawLine(xp, yp, nextXp, nextYp);
                    }catch(java.lang.NullPointerException npe){}
                }
            }
        }
    }
    
    private void BuildRack(Graphics g){
        if(grid){
            int spaceX=0;
            int spaceY=0;
            int squareMetresX=0;
            int squareMetresY=0;
            
            //La distancia de la rejilla se adapta al zoom.
            while (spaceX < 50){
                squareMetresX+=100;
                spaceX = (int) (squareMetresX * this.getWidth() / m_model.WhatBoardSize());
            }
            while (spaceY < 50){
                squareMetresY+=100;
                spaceY = (int) (squareMetresY * this.getHeight() / m_model.WhatBoardSize());
            }
            g.setColor(Color.GRAY);
            g.drawString("0 m",3,10);
            //Linea vertical.
            tagMetres = 0;
            for (int i = spaceX; i<this.getWidth(); i+=spaceX){
                g.drawLine(i,0,i,this.getHeight());
                g.drawString((tagMetres+=squareMetresX)  +" m",i+5,10);
            }
            //Linea Horizontal.
            tagMetres = 0;
            for (int j = spaceY; j<this.getHeight(); j+=spaceY){
                g.drawLine(0,j,this.getWidth(),j);
                g.drawString((tagMetres+=squareMetresY) +" m",3,j+15);
            }
            //Marcamos los limites de la simulacion NS.
            g.setColor(Color.BLACK);
            int lineX= (int) (edgex * this.getWidth() / m_model.WhatBoardSize());
            int lineY = (int) (edgey * this.getHeight() / m_model.WhatBoardSize());
            g.drawLine(lineX,0,lineX,lineY);
            g.drawLine(lineX+1,0,lineX+1,lineY);
            g.drawLine(0,lineY,lineX,lineY);
            g.drawLine(0,lineY+1,lineX,lineY+1);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        m_model.GenerateStaticVisibilityMatrix();
        BuildRack(g);
        //Ponemos los AP.
        APDrawing(g);
    }
}



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flamefeed.server.log;

import java.util.Date;

/**
 *
 * @author Anedaar
 */
public class LogEvent {
    
    public String source="NYI";
    public String target="NYI";
    public int x=0;
    public int y=0;
    public int z=0;
    public Date time=new Date();
    public String action="NYI";
    public String tool="NYI";
    public String world="NYI";
    
    public LogEvent(){
        
        
    }
    
    
}

package eu.isawsm.accelerate.server;

import javax.naming.ConfigurationException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        AxServer axServer = null;
        try {
            axServer = new AxServer();
        } catch (ConfigurationException e) {
            System.out.println("FATAL: "+e.getMessage());
        }
        axServer.start(args);
    }
}

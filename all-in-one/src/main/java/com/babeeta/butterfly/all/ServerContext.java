package com.babeeta.butterfly.all;

import com.mongodb.Mongo;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-13
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class ServerContext {
    private Mongo mongo;
    private String devRouter = "dev";
    private String gatewayRouter = "gateway.dev";
    private List<String> gatewaySecondaryRouter;
    private List<String> gatewaySecondaryRouterDB;


    public ServerContext(CommandLine cl) throws Exception {
        if (cl.getOptionValues("m") != null && cl.getOptionValues("m").length == 2) {
            mongo = new Mongo(cl.getOptionValues("m")[0], Integer.parseInt(cl.getOptionValues("m")[1]));
        } else {
            mongo = new Mongo("mongodb", 27017);
        }

        if (cl.getOptionValue("d") != null && cl.getOptionValue("d").trim().length() > 0) {
            devRouter = cl.getOptionValue("d");
        }

        if (cl.getOptionValue("g") != null && cl.getOptionValue("g").trim().length() > 0) {
            gatewayRouter = cl.getOptionValue("g");
        }

        ResourceBundle rb = ResourceBundle.getBundle("bigrouter");
        String[] gatewaySecondaryRouterArray = ((cl.getOptionValues("gs") != null && cl.getOptionValues("gs").length > 0)
                ? cl.getOptionValues("gs")
                : rb.getString("gateway.secondary.domain").split(","));

        gatewaySecondaryRouter = new ArrayList<String>();
        for (String router : gatewaySecondaryRouterArray) {
            if (router == null || router.trim().length() == 0) {
                break;
            }
            gatewaySecondaryRouter.add(router);
        }

        String[] gatewaySecondaryRouterDBArray = ((cl.getOptionValues("dn") != null && cl.getOptionValues("dn").length > 0)
                ? cl.getOptionValues("dn")
                : rb.getString("gateway.secondary.db").split(","));

        gatewaySecondaryRouterDB = new ArrayList<String>();
        for (String dbName : gatewaySecondaryRouterDBArray) {
            if (dbName == null || dbName.trim().length() == 0) {
                break;
            }
            gatewaySecondaryRouterDB.add(dbName);
        }
    }

    public Mongo getMongo() {
        return mongo;
    }

    public String getDevRouter() {
        return devRouter;
    }

    public String getGatewayRouter() {
        return gatewayRouter;
    }

    public List getGatewaySecondaryRouter() {
        return gatewaySecondaryRouter;
    }

    public List getGatewaySecondaryRouterDB() {
        return gatewaySecondaryRouterDB;
    }
}

package com.bloomberg.emsx.samples;

import java.util.ArrayList;
import com.bloomberg.emsx.samples.Notification.NotificationCategory;

public class EasyMSXAPISample implements NotificationHandler {

	EasyMSX emsx;
	
    public static void main(String[] args) throws java.lang.Exception
    {
        System.out.println("Bloomberg - EasyMSX Example - EasyMSXAPISample");

        EasyMSXAPISample example = new EasyMSXAPISample();
        example.run(args);
        System.in.read();    

    }

	private void run(String[] args) throws Exception {
	
		Log.logLevel = Log.LogLevels.NONE;

		System.out.println("Initializing EMSXAPI ");

		// Uncomment the appropriate constructor call
		emsx = new EasyMSX();
		//emsx = new EasyMSX(Environment.BETA);
		//emsx = new EasyMSX(Environment.PRODUCTION);
		
		System.out.println("EMSXAPI initialized OK");
		
		System.out.println ("\nTeams:");
		
		for(Team t: emsx.teams) {
			System.out.println(t.name);
		}
	
		System.out.println ("\nBrokers:");

		for(Broker b: emsx.brokers) {
			System.out.println("Broker: " + b.name);
			
			System.out.println("\tAsset Class: " + b.assetClass.toString());
			
			for(BrokerStrategy s: b.strategies) {
				System.out.println("\tStrategy: " + s.name);
				
				for(BrokerStrategyParameter p: s.parameters) {
					System.out.println("\t\tParameter: " + p.name);
				}
			}
		}
		
		emsx.orders.addNotificationHandler(this);
		emsx.routes.addNotificationHandler(this);
		
		//emsx.teams.get("EMSX_API").select();
		
		emsx.start();

		printOrderBlotter();
		printRouteBlotter();
	}
	
	private void printOrderBlotter() {
		
		System.out.println("Order Blotter: \n");
		
		for(Order o: emsx.orders) {
			System.out.println("\tSequence: " + o.field("EMSX_SEQUENCE").value() + "\tStatus: " + o.field("EMSX_STATUS").value() + "\t Ticker: " + o.field("EMSX_TICKER").value() + "\t Amount: " + o.field("EMSX_AMOUNT").value());
		}
	}
	
	private void printRouteBlotter() {

		System.out.println("Route Blotter:");
		
		for(Route r: emsx.routes) {
			System.out.println("\tSequence: " + r.field("EMSX_SEQUENCE").value() + "\tID: " + r.field("EMSX_ROUTE_ID").value() + "\tStatus: " + r.field("EMSX_STATUS").value()  + "\t Amount: " + r.field("EMSX_AMOUNT").value());
		}
	}

    public void processNotification(Notification notification) {

        if (notification.category == NotificationCategory.ORDER) {
            System.out.println("\nChange to Order (" + notification.type.toString() + "): " + notification.getOrder().field("EMSX_SEQUENCE").value());
            printFieldChanges(notification.getFieldChanges());
            printOrderBlotter();
        }
	    else if(notification.category==NotificationCategory.ROUTE) {
	    	System.out.println("\nChange to Route (" + notification.type.toString() + "): " + notification.getRoute().field("EMSX_SEQUENCE").value() + "/" + notification.getRoute().field("EMSX_ROUTE_ID").value());
            printFieldChanges(notification.getFieldChanges());
            printRouteBlotter();
        }
    }

    private void printFieldChanges(ArrayList<FieldChange> fieldChanges)
    {
        for (FieldChange fc:fieldChanges)
        {
        	System.out.println("Field: " + fc.field.name() + "\tOld: " + fc.oldValue + "\tNew: " + fc.newValue);
        }
    }
}

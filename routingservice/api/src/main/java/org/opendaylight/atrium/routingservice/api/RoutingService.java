/*
 * Copyright (c) 2015 Wipro Ltd. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.atrium.routingservice.api;

import java.util.Collection;

import org.opendaylight.atrium.atriumutil.IpAddress;
import org.opendaylight.atrium.atriumutil.AtriumMacAddress;
import org.opendaylight.atrium.hostservice.api.HostService;
import org.opendaylight.atrium.routingservice.bgp.api.RouteEntry;
import org.opendaylight.atrium.routingservice.bgp.api.BgpService;
import org.opendaylight.atrium.routingservice.config.api.RoutingConfigService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;

/**
 * Provides a way of interacting with the RIB management component.
 */
public interface RoutingService {

    /**
     * Starts the routing service.
     */
    public void start();
    
    
    /**
     * Set the service handles which routing service uses 
     */
    public void setServices(RoutingConfigService routingConfigService, BgpService bgpService, HostService hostService); 
    

    /**
     * Stops the routing service.
     */
    public void stop();

    /**
     * Gets all IPv4 routes
     *
     * @return the IPv4 routes
     */
    public Collection<RouteEntry> getRoutes4();

    /**
     * Gets all IPv6 routes 
     *
     * @return IPv6 routes
     */
    public Collection<RouteEntry> getRoutes6();

    /**
     * Adds FIB listener.
     *
     * @param fibListener listener to send FIB updates to
     */
    public void addFibListener(FibListener fibListener);

    
    
}

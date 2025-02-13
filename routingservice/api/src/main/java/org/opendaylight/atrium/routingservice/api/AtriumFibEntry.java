/*
 * Copyright (c) 2015 Wipro Ltd. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.atrium.routingservice.api;

import com.google.common.base.MoreObjects;
import org.opendaylight.atrium.atriumutil.IpAddress;
import org.opendaylight.atrium.atriumutil.IpPrefix;
import org.opendaylight.atrium.atriumutil.AtriumMacAddress;


import java.util.Objects;

/**
 * An entry in the Forwarding Information Base (FIB).
 */
public class AtriumFibEntry {

    private final IpPrefix prefix;
    private final IpAddress nextHopIp;
    private final AtriumMacAddress nextHopMac;

    /**
     * Creates a new FIB entry.
     *
     * @param prefix IP prefix of the FIB entry
     * @param nextHopIp IP address of the next hop
     * @param nextHopMac MAC address of the next hop
     */
    public AtriumFibEntry(IpPrefix prefix, IpAddress nextHopIp, AtriumMacAddress nextHopMac) {
        this.prefix = prefix;
        this.nextHopIp = nextHopIp;
        this.nextHopMac = nextHopMac;
    }

    /**
     * Returns the IP prefix of the FIB entry.
     *
     * @return the IP prefix
     */
    public IpPrefix prefix() {
        return prefix;
    }

    /**
     * Returns the IP address of the next hop.
     *
     * @return the IP address
     */
    public IpAddress nextHopIp() {
        return nextHopIp;
    }

    /**
     * Returns the MAC address of the next hop.
     *
     * @return the MAC address
     */
    public AtriumMacAddress nextHopMac() {
        return nextHopMac;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AtriumFibEntry)) {
            return false;
        }

        AtriumFibEntry that = (AtriumFibEntry) o;

        return Objects.equals(this.prefix, that.prefix) &&
                Objects.equals(this.nextHopIp, that.nextHopIp) &&
                Objects.equals(this.nextHopMac, that.nextHopMac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, nextHopIp, nextHopMac);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("prefix", prefix)
                .add("nextHopIp", nextHopIp)
                .add("nextHopMac", nextHopMac)
                .toString();
    }
}

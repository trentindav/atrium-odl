package org.opendaylight.atrium.routingservice.config.api;

import java.util.Objects;

import org.opendaylight.atrium.atriumutil.IpAddress;
import org.opendaylight.atrium.atriumutil.IpPrefix;

import com.google.common.base.MoreObjects;

/**
 * Configuration details for an IP prefix entry.
 */
public class LocalIpPrefixEntry {
    private final IpPrefix ipPrefix;
    private final IpPrefixType type;
    private final IpAddress gatewayIpAddress;

    /**
     * Specifies the type of local IP prefix.
     */
    public enum IpPrefixType {
        /**
         * Public IP prefixes should be exchanged by eBGP.
         */
        PUBLIC,
        /**
         * Private IP prefixes should be used only locally and not exchanged
         * by eBGP.
         */
        PRIVATE,
        /**
         * For IP prefixes in blacklist.
         */
        BLACK_LIST
    }

    /**
     * Creates a new IP prefix entry.
     *
     * @param ipPrefix         an IP prefix as a String
     * @param type             an IP prefix type as an IpPrefixType
     * @param gatewayIpAddress IP of the gateway
     */
    public LocalIpPrefixEntry(String ipPrefix,
                              IpPrefixType type,
                              IpAddress
                                      gatewayIpAddress) {
        this.ipPrefix = IpPrefix.valueOf(ipPrefix);
        this.type = type;
        this.gatewayIpAddress = gatewayIpAddress;
    }

    /**
     * Gets the IP prefix of the IP prefix entry.
     *
     * @return the IP prefix
     */
    public IpPrefix ipPrefix() {
        return ipPrefix;
    }

    /**
     * Gets the IP prefix type of the IP prefix entry.
     *
     * @return the IP prefix type
     */
    public IpPrefixType ipPrefixType() {
        return type;
    }

    /**
     * Gets the gateway IP address of the IP prefix entry.
     *
     * @return the gateway IP address
     */
    public IpAddress getGatewayIpAddress() {
        return gatewayIpAddress;
    }

    /**
     * Tests whether the IP version of this entry is IPv4.
     *
     * @return true if the IP version of this entry is IPv4, otherwise false.
     */
    public boolean isIp4() {
        return ipPrefix.isIp4();
    }

    /**
     * Tests whether the IP version of this entry is IPv6.
     *
     * @return true if the IP version of this entry is IPv6, otherwise false.
     */
    public boolean isIp6() {
        return ipPrefix.isIp6();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipPrefix, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LocalIpPrefixEntry)) {
            return false;
        }

        LocalIpPrefixEntry that = (LocalIpPrefixEntry) obj;
        return Objects.equals(this.ipPrefix, that.ipPrefix)
                && Objects.equals(this.type, that.type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("ipPrefix", ipPrefix)
                .add("ipPrefixType", type)
                .toString();
    }
}

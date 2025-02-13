package org.opendaylight.atrium.routingservice.config.api;

import java.util.Set;

import org.opendaylight.atrium.atriumutil.Interface;
import org.opendaylight.atrium.atriumutil.IpPrefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgpconfig.api.rev150725.BgpPeers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgpconfig.api.rev150725.BgpSpeakers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgpconfig.api.rev150725.bgppeers.BgpPeer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgpconfig.api.rev150725.bgpspeakers.BgpSpeaker;

public interface RoutingConfigService {
	/**
	 * Gets the list of BGP speakers inside the SDN network.
	 *
	 * @return the map of BGP speaker names to BGP speaker objects
	 */
	public BgpSpeakers getBgpSpeakers();

	/**
	 * Gets the list of configured BGP peers.
	 *
	 * @return the map from peer IP address to BgpPeer object
	 */
	public BgpPeers getBgpPeers();

	/**
	 * 
	 * @param mac
	 * @return
	 */
	public BgpSpeaker getBgpSpeakerByMac(String mac);

	/**
	 * 
	 * @param ip
	 * @return
	 */
	public BgpPeer getBgpPeerByIpAddress(IpAddress ip);

	/**
	 * Evaluates whether an IP address belongs to local SDN network.
	 *
	 * @param ipAddress
	 *            the IP address to evaluate
	 * @return true if the IP address belongs to local SDN network, otherwise
	 *         false
	 */
	public boolean isIpAddressLocal(IpAddress ipAddress);

	/**
	 * Evaluates whether an IP prefix belongs to local SDN network.
	 *
	 * @param ipPrefix
	 *            the IP prefix to evaluate
	 * @return true if the IP prefix belongs to local SDN network, otherwise
	 *         false
	 */
	public boolean isIpPrefixLocal(IpPrefix ipPrefix);

	/**
	 * Retrieves the entire set of interfaces in the network.
	 *
	 * @return the set of interfaces
	 */
	 Set<Interface> getInterfaces();

	/**
	 * Retrieves the interface associated with the given connect point.
	 *
	 * @param connectPoint
	 *            the connect point to retrieve interface information for
	 * @return the interface
	 */
	 Interface getInterface(NodeConnector connectPoint);

	/**
	 * Retrieves the interface that matches the given IP address. Matching means
	 * that the IP address is in one of the interface's assigned subnets.
	 *
	 * @param ipAddress
	 *            IP address to match
	 * @return the matching interface
	 */
	 Interface getMatchingInterface(IpAddress ipAddress);
}

/*
 * This module is inserted by wipro and it is taken from 
 * https://github.com/opendaylight/l2switch/blob/stable/lithium/hosttracker/implementation/src/main/java/org/opendaylight/l2switch/hosttracker/plugin/internal/SimpleAddressObserver.java
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.atrium.hostservice.impl;

import java.math.BigInteger;
import java.util.Date;

import org.opendaylight.controller.sal.binding.api.NotificationService;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.arp.rev140528.ArpPacketListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.arp.rev140528.ArpPacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.arp.rev140528.arp.packet.received.packet.chain.packet.ArpPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.PacketChain;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.packet.chain.packet.RawPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.KnownEtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.VlanId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.ethernet.packet.received.packet.chain.packet.EthernetPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv4.rev140528.Ipv4PacketListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv4.rev140528.Ipv4PacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv4.rev140528.ipv4.packet.received.packet.chain.packet.Ipv4Packet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv6.rev140528.Ipv6PacketListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv6.rev140528.Ipv6PacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv6.rev140528.ipv6.packet.received.packet.chain.packet.Ipv6Packet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hostservice.api.rev150725.address.node.connector.ConnectorAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hostservice.api.rev150725.address.node.connector.ConnectorAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hostservice.api.rev150725.address.node.connector.ConnectorAddressKey;

/**
 * A Simple Address Observer based on l2switch address observer.
 */
public class AddressObserver implements ArpPacketListener, Ipv4PacketListener, Ipv6PacketListener {

	private final String IPV4_IP_TO_IGNORE = "0.0.0.0";
	private final String IPV6_IP_TO_IGNORE = "0:0:0:0:0:0:0:0";

	private HostMonitor hostMonitor;
	private NotificationService notificationService;

	public AddressObserver(HostMonitor hostTrackerImpl, NotificationService notificationService) {
		this.hostMonitor = hostTrackerImpl;
		this.notificationService = notificationService;

	}

	void registerAsNotificationListener() {
		this.notificationService.registerNotificationListener(this);
	}

	@Override
	public void onArpPacketReceived(ArpPacketReceived packetReceived) {
		if (packetReceived == null || packetReceived.getPacketChain() == null) {
			return;
		}

		RawPacket rawPacket = null;
		EthernetPacket ethernetPacket = null;
		ArpPacket arpPacket = null;
		for (PacketChain packetChain : packetReceived.getPacketChain()) {
			if (packetChain.getPacket() instanceof RawPacket) {
				rawPacket = (RawPacket) packetChain.getPacket();
			} else if (packetChain.getPacket() instanceof EthernetPacket) {
				ethernetPacket = (EthernetPacket) packetChain.getPacket();
			} else if (packetChain.getPacket() instanceof ArpPacket) {
				arpPacket = (ArpPacket) packetChain.getPacket();
			}
		}
		if (rawPacket == null || ethernetPacket == null || arpPacket == null) {
			return;
		}
		VlanId vlanId = null;
		if (ethernetPacket.getEthertype().equals(KnownEtherType.VlanTagged)) {
			vlanId = ethernetPacket.getHeader8021q().get(0).getVlan();
		}
		MacAddress sourceMac = ethernetPacket.getSourceMac();
		IpAddress ipAddress = null;
		if (arpPacket.getProtocolType().equals(KnownEtherType.Ipv4)) {
			ipAddress = new IpAddress(new Ipv4Address(arpPacket.getSourceProtocolAddress()));
		}
		ConnectorAddress addrs = createAddresses(sourceMac, vlanId, ipAddress, ethernetPacket.getEthertype());
		if (addrs == null) {
			return;
		}
		NodeConnectorRef ingress = rawPacket.getIngress();
		hostMonitor.packetReceived(addrs, ingress.getValue());
	}

	@Override
	public void onIpv4PacketReceived(Ipv4PacketReceived packetReceived) {
		if (packetReceived == null || packetReceived.getPacketChain() == null) {
			return;
		}

		RawPacket rawPacket = null;
		EthernetPacket ethernetPacket = null;
		Ipv4Packet ipv4Packet = null;
		for (PacketChain packetChain : packetReceived.getPacketChain()) {
			if (packetChain.getPacket() instanceof RawPacket) {
				rawPacket = (RawPacket) packetChain.getPacket();
			} else if (packetChain.getPacket() instanceof EthernetPacket) {
				ethernetPacket = (EthernetPacket) packetChain.getPacket();
			} else if (packetChain.getPacket() instanceof Ipv4Packet) {
				ipv4Packet = (Ipv4Packet) packetChain.getPacket();
			}
		}
		if (rawPacket == null || ethernetPacket == null || ipv4Packet == null) {
			return;
		}

		if (IPV4_IP_TO_IGNORE.equals(ipv4Packet.getSourceIpv4().getValue())) {
			return;
		}

		VlanId vlanId = null;
		if (ethernetPacket.getEthertype().equals(KnownEtherType.VlanTagged)) {
			vlanId = ethernetPacket.getHeader8021q().get(0).getVlan();
		}
		MacAddress sourceMac = ethernetPacket.getSourceMac();
		IpAddress ipAddress = new IpAddress(ipv4Packet.getSourceIpv4());

		ConnectorAddress addrs = createAddresses(sourceMac, vlanId, ipAddress, ethernetPacket.getEthertype());
		if (addrs == null) {
			return;
		}
		NodeConnectorRef ingress = rawPacket.getIngress();
		hostMonitor.packetReceived(addrs, ingress.getValue());
	}

	@Override
	public void onIpv6PacketReceived(Ipv6PacketReceived packetReceived) {
		if (packetReceived == null || packetReceived.getPacketChain() == null) {
			return;
		}

		RawPacket rawPacket = null;
		EthernetPacket ethernetPacket = null;
		Ipv6Packet ipv6Packet = null;
		for (PacketChain packetChain : packetReceived.getPacketChain()) {
			if (packetChain.getPacket() instanceof RawPacket) {
				rawPacket = (RawPacket) packetChain.getPacket();
			} else if (packetChain.getPacket() instanceof EthernetPacket) {
				ethernetPacket = (EthernetPacket) packetChain.getPacket();
			} else if (packetChain.getPacket() instanceof Ipv6Packet) {
				ipv6Packet = (Ipv6Packet) packetChain.getPacket();
			}
		}
		if (rawPacket == null || ethernetPacket == null || ipv6Packet == null) {
			return;
		}
		if (IPV6_IP_TO_IGNORE.equals(ipv6Packet.getSourceIpv6().getValue())) {
			return;
		}

		VlanId vlanId = null;
		if (ethernetPacket.getEthertype().equals(KnownEtherType.VlanTagged)) {
			vlanId = ethernetPacket.getHeader8021q().get(0).getVlan();
		}
		MacAddress sourceMac = ethernetPacket.getSourceMac();
		IpAddress ipAddress = new IpAddress(ipv6Packet.getSourceIpv6());

		ConnectorAddress addrs = createAddresses(sourceMac, vlanId, ipAddress, ethernetPacket.getEthertype());
		if (addrs == null) {
			return;
		}
		NodeConnectorRef ingress = rawPacket.getIngress();
		hostMonitor.packetReceived(addrs, ingress.getValue());
	}

	private ConnectorAddress createAddresses(MacAddress srcMacAddr, VlanId vlanId, IpAddress srcIpAddr,
			KnownEtherType ketype) {
		ConnectorAddressBuilder addrs = new ConnectorAddressBuilder();
		if (srcMacAddr == null || srcIpAddr == null) {
			return null;
		}
		/**
		 * TODO: if this is used, use a ReadWriteTranscation to figure out if
		 * there is an already existing addresses that has the same MAC, IP,
		 * VLAN triple and use it’s ID then, if there’s none, then we make up
		 * our own Addresses
		 */
		BigInteger id = BigInteger.valueOf(ketype.getIntValue()).abs()
				.add(BigInteger.valueOf(srcMacAddr.hashCode()).abs().shiftLeft(16));
		addrs.setId(id);
		addrs.setKey(new ConnectorAddressKey(addrs.getId()));
		addrs.setVlan(vlanId);
		addrs.setIp(srcIpAddr);
		addrs.setMac(srcMacAddr);
		// addrs.setFirstSeen(new Date().getTime());
		addrs.setLastSeen(new Date().getTime());
		return addrs.build();
	}
}

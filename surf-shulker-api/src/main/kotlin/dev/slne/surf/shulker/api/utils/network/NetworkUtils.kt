package dev.slne.surf.shulker.api.utils.network

import java.net.Inet4Address
import java.net.NetworkInterface

fun localAddress(): String {
    NetworkInterface.getNetworkInterfaces().toList().forEach { iface ->
        if (!iface.isLoopback && iface.isUp) {
            iface.inetAddresses.toList().forEach { address ->
                if (address is Inet4Address && !address.isLoopbackAddress) {
                    return address.hostAddress
                }
            }
        }
    }

    return "null"
}
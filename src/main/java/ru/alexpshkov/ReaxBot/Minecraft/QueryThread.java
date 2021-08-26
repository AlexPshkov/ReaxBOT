package ru.alexpshkov.ReaxBot.Minecraft;

import ru.alexpshkov.ReaxBot.ReaxTelegramBot;
import ru.alexpshkov.ReaxBot.Utils.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryThread extends Thread{

    private List<String> onlineUsernames;
    private List<String> currentPlayers;

    private final String serverName;
    private final InetSocketAddress queryAddress;

    public QueryThread(String serverName, String host, int port) {
        this.queryAddress = new InetSocketAddress(host, port);
        this.serverName = serverName;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(5000);
                sendQueryRequest();
                List<String> newCurrentPlayers = onlineUsernames;
                if (currentPlayers == null) currentPlayers = newCurrentPlayers;
                for (String player : newCurrentPlayers)
                    if (!currentPlayers.contains(player)) ReaxTelegramBot.broadcastPlayerJoin(serverName + " | ", player, newCurrentPlayers);
                for (String player : currentPlayers)
                    if (!newCurrentPlayers.contains(player)) ReaxTelegramBot.broadcastPlayerQuit(serverName + " | ", player, newCurrentPlayers);
                currentPlayers = newCurrentPlayers;
            } catch (Exception e) {
                Logger.printError(e);
            }
        }
    }

    public void sendQueryRequest() {
        InetSocketAddress local = queryAddress;
        try (DatagramSocket socket = new DatagramSocket()) {
            final byte[] receiveData = new byte[10240];
            socket.setSoTimeout(2000);
            sendPacket(socket, local, 0xFE, 0xFD, 0x09, 0x01, 0x01, 0x01, 0x01);
            final int challengeInteger;
            {
                receivePacket(socket, receiveData);
                byte byte1 = -1;
                int i = 0;
                byte[] buffer = new byte[11];
                for (int count = 5; (byte1 = receiveData[count++]) != 0; )
                    buffer[i++] = byte1;
                challengeInteger = Integer.parseInt(new String(buffer).trim());
            }
            sendPacket(socket, local, 0xFE, 0xFD, 0x00, 0x01, 0x01, 0x01, 0x01, challengeInteger >> 24, challengeInteger >> 16, challengeInteger >> 8, challengeInteger, 0x00, 0x00, 0x00, 0x00);

            final int length = receivePacket(socket, receiveData).getLength();
            Map<String, String> values = new HashMap<>();
            final AtomicInteger cursor = new AtomicInteger(5);
            while (cursor.get() < length) {
                final String s = readString(receiveData, cursor);
                if (s.length() == 0)
                    break;
                else {
                    final String v = readString(receiveData, cursor);
                    values.put(s, v);
                }
            }
            readString(receiveData, cursor);
            final List<String> players = new ArrayList<>();
            while (cursor.get() < length) {
                final String name = readString(receiveData, cursor);
                if (name.length() > 0)
                    players.add(name);
            }
            onlineUsernames = players;
        } catch (Exception ex) {
            Logger.printError(ex);
        }
    }

    private final static void sendPacket(DatagramSocket socket, InetSocketAddress targetAddress, byte... data) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, targetAddress.getAddress(), targetAddress.getPort());
        socket.send(sendPacket);
    }

    private final static void sendPacket(DatagramSocket socket, InetSocketAddress targetAddress, int... data) throws IOException {
        final byte[] d = new byte[data.length];
        int i = 0;
        for(int j : data)
            d[i++] = (byte)(j & 0xff);
        sendPacket(socket, targetAddress, d);
    }

    private final static DatagramPacket receivePacket(DatagramSocket socket, byte[] buffer) throws IOException {
        final DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        socket.receive(dp);
        return dp;
    }

    private final static String readString(byte[] array, AtomicInteger cursor) {
        final int startPosition = cursor.incrementAndGet();
        for(; cursor.get() < array.length && array[cursor.get()] != 0; cursor.incrementAndGet())
            ;
        return new String(Arrays.copyOfRange(array, startPosition, cursor.get()));
    }
}
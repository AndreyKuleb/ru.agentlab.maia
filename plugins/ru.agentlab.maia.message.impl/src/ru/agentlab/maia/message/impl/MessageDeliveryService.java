package ru.agentlab.maia.message.impl;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.agentlab.maia.agent.AgentAddress;
import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IAgentRegistry;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.LocalAgentAddress;
import ru.agentlab.maia.agent.RemoteAgentAddress;
import ru.agentlab.maia.message.IMessageDeliveryService;
import ru.agentlab.maia.message.INetworkProtocol;

public abstract class MessageDeliveryService implements IMessageDeliveryService {

	@Inject
	IAgentRegistry registry;

	protected ServerBootstrap serverBootstrap;

	protected Bootstrap bootstrap;

	@PostConstruct
	public void setup(@Named("worker-group") EventLoopGroup workerGroup,
			@Named("boss-group") EventLoopGroup bossGroup) {
		INetworkProtocol networkProtocol = getNetworkProtocol();
		bootstrap = networkProtocol.getClientBootstrap(workerGroup);
		serverBootstrap = networkProtocol
			.getServerBootstrap(workerGroup, bossGroup, new MessageToMessageDecoder<AclMessage>() {
				@Override
				protected void decode(ChannelHandlerContext ctx, AclMessage msg, List<Object> arg2) throws Exception {
					send(msg);
					ctx.close();
				}
			});
		startServer();
	}

	@Override
	public void send(IMessage message) {
		UUID receiver = message.getReceiver();
		message.setPostTimeStamp(LocalDateTime.now());
		AgentAddress address = registry.get(receiver);
		if (address == null) {
			throw new RuntimeException("Agent [" + receiver + "] did not found");
		}

		System.out.println(
			"[" + message.getSender() + "] " + "-------- " + message.getPerformative() + "(" + message.getContent()
					+ ")" + " --------> " + "[" + message.getReceiver() + "]");

		if (address instanceof LocalAgentAddress) {
			IAgent agent = ((LocalAgentAddress) address).getAgent();
			sendInternal(message, agent);
		} else if (address instanceof RemoteAgentAddress) {
			sendExternal(message, ((RemoteAgentAddress) address).getAddress());
		}
	}

	protected void sendInternal(IMessage message, IAgent agent) {
		agent.notify(message);
	}

	protected void sendExternal(IMessage message, SocketAddress address) {
		bootstrap.connect(address).addListener((ChannelFuture future) -> {
			if (future.isSuccess()) {
				Channel channel = future.channel();
				if (channel.isWritable()) {
					channel.writeAndFlush(message);
					channel.close();
				}
			} else {
				// clientBootstrap.releaseExternalResources();
				// throw new ConnectionException("Something bad
				// happened...");
			}
		});
	}

	public abstract INetworkProtocol getNetworkProtocol();

	public abstract void startServer();

}
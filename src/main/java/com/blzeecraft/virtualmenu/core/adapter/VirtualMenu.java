package com.blzeecraft.virtualmenu.core.adapter;

import com.blzeecraft.virtualmenu.core.IUser;
import com.blzeecraft.virtualmenu.core.item.AbstractItem;
import com.blzeecraft.virtualmenu.core.logger.LogNode;
import com.blzeecraft.virtualmenu.core.logger.PluginLogger;
import com.blzeecraft.virtualmenu.core.menu.IMenuType;
import com.blzeecraft.virtualmenu.core.module.PacketHandler;
import com.blzeecraft.virtualmenu.core.packet.AbstractPacketCloseWindow;
import com.blzeecraft.virtualmenu.core.packet.AbstractPacketSetSlot;
import com.blzeecraft.virtualmenu.core.packet.AbstractPacketWindowItems;
import com.blzeecraft.virtualmenu.core.packet.AbstractPacketWindowOpen;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * 代表 VirtualMenu 核心, 用来转发对适配器单例的调用.
 * 
 * @author colors_wind
 * @see com.blzeecraft.virtualmenu.core.adapter.IPacketAdapter
 * @see com.blzeecraft.virtualmenu.core.adapter.IPlatformAdapter
 */
@UtilityClass
public class VirtualMenu {

	@NonNull
	public static void setPacketAdapter(IPacketAdapter adapter) {
		packetAdapter = check("Packetdapter", packetAdapter, adapter);
	}

	@NonNull
	public static void setPlatformAdapter(IPlatformAdapter adapter) {
		platformAdapter = check("PlatformAdapter", platformAdapter, adapter);
	}

	private static <T> T check(@NonNull String name, T origin, @NonNull T adapter) {
		if (origin == null) {
			PluginLogger.info(LogNode.ROOT, "已经设置 " + name + " 为 " + adapter.getClass().getTypeName());
			return adapter;
		}
		throw new IllegalArgumentException(name + "已经设置为 " + packetAdapter.getClass().getTypeName() + " 不能再次设置");

	}

	private static IPacketAdapter packetAdapter;
	private static IPlatformAdapter platformAdapter;

	@NonNull
	public AbstractPacketCloseWindow<?> createPacketCloseWindow(IUser<?> user, int windowId) {
		return packetAdapter.createPacketCloseWindow(user, windowId);
	}

	@NonNull
	public AbstractPacketWindowOpen<?> createPacketWindOpen(IUser<?> user, int windowId, IMenuType type, String title) {
		return packetAdapter.createPacketWindOpen(user, windowId, type, title);
	}

	@NonNull
	public AbstractPacketSetSlot<?> createPacketSetSlot(IUser<?> user, int windowId, int slot, AbstractItem<?> item) {
		return packetAdapter.createPacketSetSlot(user, windowId, slot, item);
	}

	@NonNull
	public AbstractPacketWindowItems<?> createPacketWindowItems(IUser<?> user, int windowId, AbstractItem<?>[] items) {
		return packetAdapter.createPacketWindowItems(user, windowId, items);
	}

	@NonNull
	public boolean registerPacketHandler(PacketHandler handler) {
		return packetAdapter.registerPacketHandler(handler);
	}

	@NonNull
	public void sendMessage(IUser<?> user, BaseComponent... component) {
		platformAdapter.sendMessage(user, component);
	}

	@NonNull
	public void sendMessage(IUser<?> user, String msg) {
		platformAdapter.sendMessage(user, msg);
	}

	@NonNull
	public boolean hasPermission(IUser<?> user, String permission) {
		return platformAdapter.hasPermission(user, permission);
	}

	@NonNull
	public void sendActionbar(IUser<?> user, String actionbar) {
		platformAdapter.sendActionbar(user, actionbar);
	}

	@NonNull
	public void sendTitle(IUser<?> user, String title) {
		platformAdapter.sendTitle(user, title);
	}

	@NonNull
	public void sendTitle(IUser<?> user, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		platformAdapter.sendTitle(user, title, subTitle, fadeIn, stay, fadeOut);
	}

	@NonNull
	public void performCommand(IUser<?> user, String command) {
		platformAdapter.performCommand(user, command);
	}

	@NonNull
	public void performCommandAsAdmin(IUser<?> user, String command) {
		platformAdapter.performCommandAsAdmin(user, command);
	}

	@NonNull
	public void performCommandAsConsole(String command) {
		platformAdapter.performCommandAsConsole(command);
	}

	public void updateInventory(IUser<?> user) {
		platformAdapter.updateInventory(user);
	}

	@NonNull
	@SuppressWarnings("unchecked")
	public <T> AbstractItem<T> emptyItem() {
		return (AbstractItem<T>) platformAdapter.emptyItem();
	}

}

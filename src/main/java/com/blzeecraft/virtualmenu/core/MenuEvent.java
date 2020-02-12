package com.blzeecraft.virtualmenu.core;

import com.blzeecraft.virtualmenu.core.menu.ClickType;
import com.blzeecraft.virtualmenu.core.menu.IPacketMenu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author colors_wind
 * @date 2020-02-10
 */
@AllArgsConstructor
@ToString
@Getter
public abstract class MenuEvent {
	
	protected final UserSession session;
	
	
	public ClickType getClickType() {
		return ClickType.UNKNOWN;
	}
	
	public IUser<?> getUser() {
		return session.getUser();
	}
	
	public IPacketMenu getMenu() {
		return session.getMenu();
	}

}

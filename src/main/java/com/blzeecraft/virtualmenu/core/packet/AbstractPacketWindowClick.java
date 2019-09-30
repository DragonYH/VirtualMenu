package com.blzeecraft.virtualmenu.core.packet;


import com.blzeecraft.virtualmenu.core.IUser;
import com.blzeecraft.virtualmenu.core.menu.AbstractItem;
import com.blzeecraft.virtualmenu.core.menu.ClickType;
import com.blzeecraft.virtualmenu.packet.ClickMode;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@NonNull
@Getter
@ToString(callSuper = true)
public abstract class AbstractPacketWindowClick<T> extends AbstractPacket<T> {

	protected final int windowId;
	protected final int rawSlot;
	protected final ClickType clickType;
	protected final AbstractItem<?> clickedItem;
	
	/**
	 * 如果该选项为true，拦截此数据包
	 */
	@Setter
	private boolean cancel;
	

	public AbstractPacketWindowClick(T handle, IUser<?> user, int windowId, int rawSlot, ClickType clickType, AbstractItem<?> clickedItem) {
		super(handle, user);
		this.windowId = windowId;
		this.rawSlot = rawSlot;
		this.clickType = clickType;
		this.clickedItem = clickedItem;

	}
	
	
	public ClickType getClickType(ClickMode mode, int button, int slot){
		switch(mode) {
		case PICKUP: 
			if (button == 0) {
				return ClickType.LEFT;
			} else if (button == 1) {
				return ClickType.RIGHT;
			}
			break;
		case QUICK_MOVE:
			if (button == 0) {
				return ClickType.SHIFT_LEFT;
			} else if (button == 1) {
				return ClickType.SHIFT_RIGHT;
			}
			break;
		case SWAP:
			return ClickType.NUMBER_KEY;
		case CLONE:
			return ClickType.MIDDLE;
		case THROW:
			if (slot >= 0) {
				if (button == 0) {
					return ClickType.DROP;
				} else if (button == 1) {
					return ClickType.CONTROL_DROP;
				}
			} else if (slot == -999) {
				if (button == 0) {
					return ClickType.WINDOW_BORDER_LEFT;
				} else if (button == 1) {
					return ClickType.WINDOW_BORDER_RIGHT;
				}
			}
			break;
		case QUICK_CRAFT:
			if (slot >= 0) {
				if (button == 1) {
					return ClickType.LEFT;
				} else if (button == 5) {
					return ClickType.RIGHT;
				}
			} else if (slot == -999) {
				switch(button) {
				case 0:
				case 1:
				case 2:
					return ClickType.LEFT;
				case 4:
				case 5:
				case 6:
					return ClickType.RIGHT;
				case 8:
				case 9:
				case 10:
					return ClickType.MIDDLE;
				}
			}
		case PICKUP_ALL:
			return ClickType.DOUBLE_CLICK;
		}
		return ClickType.UNKNOWN;
	}

}

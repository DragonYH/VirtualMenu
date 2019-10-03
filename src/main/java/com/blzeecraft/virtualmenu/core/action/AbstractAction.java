package com.blzeecraft.virtualmenu.core.action;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import com.blzeecraft.virtualmenu.core.IUser;
import com.blzeecraft.virtualmenu.core.config.ResolvedLineConfig;
import com.blzeecraft.virtualmenu.core.logger.LogNode;
import com.blzeecraft.virtualmenu.core.logger.LoggerObject;
import com.blzeecraft.virtualmenu.core.menu.ClickEvent;
import com.blzeecraft.virtualmenu.core.menu.ClickType;

import lombok.val;

public abstract class AbstractAction implements LoggerObject, Consumer<ClickEvent> {

	protected final LogNode node;
	protected final Set<ClickType> types;

	
	public AbstractAction(LogNode node, ResolvedLineConfig rlc) {
		this.node = node;
		this.types = rlc.getAsOptString("click").map(s -> {
			val str = new StringTokenizer(s, "-");
			val set = new HashSet<ClickType>(); //临时储存
			while(str.hasMoreTokens()) {
				set.add(ClickType.valueOf(str.nextToken().toUpperCase()));
			}
			return EnumSet.copyOf(set);
		}).orElse(EnumSet.allOf(ClickType.class));
	}
	
	@Override
	public void accept(ClickEvent e) {
		if (types.contains(e.getType())) {
			execute(e.getUser());
		}
	}

	public AbstractAction(LogNode node) {
		this.node = node;
		this.types = EnumSet.allOf(ClickType.class);
	}
	
	@Override
	public LogNode getLogNode() {
		return node;
	}
	

	public abstract void execute(IUser<?> user);

}

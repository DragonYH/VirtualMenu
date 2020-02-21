package com.blzeecraft.virtualmenu.core.command;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blzeecraft.virtualmenu.core.VirtualMenu;
import com.blzeecraft.virtualmenu.core.conf.menu.MenuManager;
import com.blzeecraft.virtualmenu.core.logger.PluginLogger;
import com.blzeecraft.virtualmenu.core.menu.IPacketMenu;
import com.blzeecraft.virtualmenu.core.user.IUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 代表一个一个合法的 CommandHandler.
 * 
 * @author colors_wind
 * @date 2020-02-15
 */
@AllArgsConstructor
@Getter
@ToString
public class CommandMeta {
	public static final Map<Class<?>, Function<String, Object>> AVAILABLE_PARSERS = new HashMap<>();
	static {
		// String
		AVAILABLE_PARSERS.put(String.class, s -> s);

		// Integer
		AVAILABLE_PARSERS.put(Integer.class, s -> {
			try {
				return Integer.valueOf(s);
			} catch (NumberFormatException e) {
				throw new IllegalCommandArgumentException("§a无法将 %arg% 转换成一个整数.", e);
			}
		});

		// Long
		AVAILABLE_PARSERS.put(Long.class, s -> {
			try {
				return Long.valueOf(s);
			} catch (NumberFormatException e) {
				throw new IllegalCommandArgumentException("§a无法将 %arg% 转换成一个长整数.", e);
			}
		});

		// Double
		AVAILABLE_PARSERS.put(Long.class, s -> {
			try {
				return Double.valueOf(s);
			} catch (NumberFormatException e) {
				throw new IllegalCommandArgumentException("§a无法将 %arg% 转换成一个浮点数(小数).", e);
			}
		});

		// IUser
		AVAILABLE_PARSERS.put(IUser.class, s -> {
			return VirtualMenu.getUserExact(s).orElseThrow(() -> new IllegalCommandArgumentException("§a找不到玩家 %arg%."));
		});

		// IPacketMenu
		AVAILABLE_PARSERS.put(IPacketMenu.class, s -> {
			return MenuManager.getMenu(s).orElseThrow(() -> new IllegalCommandArgumentException("§a找不到菜单 %arg%."));
		});
	}

	public static List<CommandMeta> analysis(Object instance) {
		return Arrays.stream(instance.getClass().getMethods()).filter(method -> method.getAnnotation(Usage.class) != null)
				.map(method -> analysis(instance, method)).collect(Collectors.toList());
	}

	public static CommandMeta analysis(Object instance, Method method) {
		boolean playerOnly = method.getAnnotation(PlayerOnly.class) != null;
		String usage = method.getAnnotation(Usage.class).value();
		Optional<String> requirePermission = Optional.ofNullable(method.getAnnotation(RequirePermission.class))
				.map(RequirePermission::value);
		// method parameters
		// arg parser = {parser0, parser1, ..., parser(n-2)}
		// command args = callstack, arg0, arg1, ..., arg(n-1)
		Class<?>[] parameterTypes = method.getParameterTypes();
		@SuppressWarnings("unchecked")
		Function<String, Object>[] argsParsers = new Function[parameterTypes.length -1];
		for (int i = 0; i < argsParsers.length; i++) {
			Function<String, Object> parser = AVAILABLE_PARSERS.get(parameterTypes[i + 1]);
			if (parser == null) {
				throw new IllegalArgumentException(
						"方法: " + method.toString() + " 不是一个合法的 CommandHandler. 调试信息: 不被支持的参数类型: " + parameterTypes[i]
								+ ". 仅支持: " + AVAILABLE_PARSERS.keySet());
			}
			argsParsers[i] = parser;
		}
		return new CommandMeta(instance, method, playerOnly, usage, requirePermission, parameterTypes, argsParsers);
	}

	private final Object instance;
	private final Method method;

	private final boolean playerOnly;
	private final String usage;
	private final Optional<String> requirePermission;
	private final Class<?>[] requireArgs;

	private final Function<String, Object>[] argsParsers;

	public int getRequireArgsCount() {
		return requireArgs.length;
	}

	public boolean visable() {
		return !usage.isEmpty();
	}

	public boolean invoke(IUser<?> sender, String[] args) {
		// args: arg1, arg2 ...
		// parameters: callstack, arg1, arg2, ...
		if (sender.isConsole() && this.playerOnly) {
			sender.sendMessageWithPrefix("§c该命令只能由玩家执行.");
			return true;
		}
		if (this.requirePermission.isPresent() && !sender.hasPermission(this.requirePermission.get())) {
			sender.sendMessageWithPrefix("§c你没有权限执行该命令.");
			return true;
		}
		Object[] parameters = new Object[args.length + 1];
		for (int i = 0; i < args.length; i++) {
			try {
				parameters[i + 1] = this.argsParsers[i].apply(args[i]);
			} catch (IllegalCommandArgumentException e) {
				sender.sendMessageWithPrefix(e.format(i, args[i]));
				return false;
			}
		}
		Callstack callstack = new Callstack(sender, args);
		try {
			method.invoke(callstack, parameters);
		} catch (Exception e) {
			PluginLogger.warning(CommandHandler.LOG_NODE, "调用 " + method.toString() + "时出错. 调试信息: " + this.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

}

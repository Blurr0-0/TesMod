package net.blurr.test_mod.event;

import net.blurr.test_mod.TestMod;
import net.blurr.test_mod.command.CalcCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID)
public class ModCommandEvents {
	@SubscribeEvent
	public static void onCommandsRegister(RegisterCommandsEvent event) {

		new CalcCommand(event.getDispatcher());
		
		ConfigCommand.register(event.getDispatcher());
	}
}

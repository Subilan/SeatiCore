package cc.seati.PlayerStats.Events;

import cc.seati.PlayerStats.Commands.CommandManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class CommandEvents {
    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent e) {
        CommandDispatcher<CommandSourceStack> dispatcher = e.getDispatcher();
        CommandManager.register(dispatcher);
    }
}
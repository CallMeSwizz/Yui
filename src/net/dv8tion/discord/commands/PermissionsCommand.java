package net.dv8tion.discord.commands;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import me.itsghost.jdiscord.events.UserChatEvent;
import me.itsghost.jdiscord.message.MessageBuilder;
import net.dv8tion.discord.Permissions;

public class PermissionsCommand extends Command
{

    @Override
    public void onChat(UserChatEvent e)
    {
        if (!containsCommand(e.getMsg()))
            return;

//        if (!Permissions.getPermissions().isOp(e.getUser()))
//        {
//            e.getGroup().sendMessage(new MessageBuilder()
//                .addUserTag(e.getUser(), e.getGroup())
//                .addString(": " + "You do not have permission to run this command! (OP required).")
//                .build());
//            return;
//        }

        String[] args = commandArgs(e.getMsg());
        if (args[0].contains(".perms") || args[0].contains(".permissions"))
        {
            args = ArrayUtils.subarray(args, 1, args.length);
        }
        else
        {
            args[0] = args[0].replace(".", "");
        }
        switch (args[0])
        {
            //Only 1 case for now. Later we will have more user permissions types...probably.
            case "op":
                processOp(args, e);
                break;
            default:
                e.getGroup().sendMessage(new MessageBuilder()
                    .addUserTag(e.getUser(), e.getGroup())
                    .addString(": " + "**Improper syntax, unrecognized argument:** " + args[1])
                    .addString("\n**Provided Command:** " + e.getMsg().toString())
                    .build());
                return;
        }
        //CommandSyntax:  .perms op add @<name>  .perms op remove @<name>  .perms op list
        //Or:   .op add @<name>   .op remove @<name>
    }

    @Override
    public List<String> aliases()
    {
        return Arrays.asList(new String[] {".perms", ".permissions", ".op"});
    }

    @Override
    public String commandDescription()
    {
        return "Used to modify the permissions of the provided user.";
    }

    @Override
    public String helpMessage()
    {
        return null;
    }

    private void processOp(String[] args, UserChatEvent e)
    {
        if (args.length == 3)
        {
            if (args[1].equals("add"))
            {
                processAddOp(args, e);
            }
            else if (args[1].equals("remove"))
            {

            }
            else
            {
                e.getGroup().sendMessage(new MessageBuilder()
                    .addUserTag(e.getUser(), e.getGroup())
                    .addString(": " + "**Improper syntax, unrecognized argument:** " + args[1])
                    .addString("\n**Provided Command:** " + e.getMsg().toString())
                    .build());
                return;
            }
        }
        else if (args.length == 2 && args[1].equals("list"))
        {
            String ops = "";
            for (String op : Permissions.getPermissions().getOps())
            {
                ops += "<@" + op + "> ";
            }
            e.getGroup().sendMessage(new MessageBuilder()
                .addUserTag(e.getUser(), e.getGroup())
                .addString(": My OPs are: [" + ops.trim() + "]")
                .build());
            return;
        }
    }

    private void processAddOp(String[] args, UserChatEvent e)
    {
        Pattern idPattern = Pattern.compile("(?<=<@)[0-9]{18}(?=>)");
        Matcher idMatch = idPattern.matcher(args[2]);
        if (!idMatch.find())
        {
            e.getGroup().sendMessage(new MessageBuilder()
                .addUserTag(e.getUser(), e.getGroup())
                .addString(": " + "Sorry, I don't recognize the user provided: " + args[2])
                .build());
            return;
        }
        try
        {
            if (Permissions.getPermissions().addOp(idMatch.group()))
            {
                e.getGroup().sendMessage(new MessageBuilder()
                    .addUserTag(e.getUser(), e.getGroup())
                    .addString(": " + "Successfully added ")
                    .addUserTag(e.getServer().getGroupUserById(idMatch.group()), e.getGroup())
                    .addString(" to the OPs list!")
                    .build());
                return;
            }
            else
            {
                e.getGroup().sendMessage(new MessageBuilder()
                    .addUserTag(e.getUser(), e.getGroup())
                    .addString(": ")
                    .addUserTag(e.getServer().getGroupUserById(idMatch.group()), e.getGroup())
                    .addString(" is already an OP!")
                    .build());
                return;
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}

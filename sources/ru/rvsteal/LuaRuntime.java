package ru.rvsteal;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JseMathLib;

final class LuaRuntime {
   private final Globals globals = new Globals();
   private final LuaValue program;
   private final LuaRuntime.Budget budget = new LuaRuntime.Budget();
   private int messages;

   LuaRuntime(String source, LuaRuntime.Game game) {
      this.globals.load(new BaseLib());
      LuaTable pkg = new LuaTable();
      pkg.set("loaded", new LuaTable());
      this.globals.set("package", pkg);
      this.globals.load(new JseMathLib());
      this.globals.set("package", LuaValue.NIL);

      for (String name : new String[]{"dofile", "loadfile", "load", "loadstring", "require", "pcall", "xpcall", "print", "collectgarbage"}) {
         this.globals.set(name, LuaValue.NIL);
      }

      LuaC.install(this.globals);
      this.globals.debuglib = this.budget;
      this.globals.set("health", new ZeroArgFunction() {
         public LuaValue call() {
            return LuaValue.valueOf(game.health());
         }
      });
      this.globals.set("hunger", new ZeroArgFunction() {
         public LuaValue call() {
            return LuaValue.valueOf(game.hunger());
         }
      });
      this.globals.set("module", new TwoArgFunction() {
         public LuaValue call(LuaValue name, LuaValue enabled) {
            return LuaValue.valueOf(game.module(name.checkjstring(), enabled.checkboolean()));
         }
      });
      this.globals.set("message", new OneArgFunction() {
         public LuaValue call(LuaValue text) {
            if (++LuaRuntime.this.messages > 1) {
               throw new LuaError("Не больше одного сообщения за тик");
            }

            game.message(text.checkjstring());
            return NIL;
         }
      });
      this.program = this.globals.load(source, "custom-script");
   }

   void tick() {
      this.budget.remaining = 10000;
      this.messages = 0;
      this.program.call();
   }

   private static final class Budget extends DebugLib {
      int remaining;

      public void onCall(LuaFunction function) {
      }

      public void onCall(LuaClosure closure, Varargs args, LuaValue[] stack) {
      }

      public void onReturn() {
      }

      public String traceback(int level) {
         return "";
      }

      public void onInstruction(int pc, Varargs v, int top) {
         if (--this.remaining < 0) {
            throw new LuaError("Превышен лимит инструкций за тик");
         }
      }
   }

   interface Game {
      double health();

      int hunger();

      boolean module(String var1, boolean var2);

      void message(String var1);
   }
}

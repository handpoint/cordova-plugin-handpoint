using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;

using System;

namespace WPCordovaClassLib.Cordova.Commands
{
    public class HandpointApiCordova : BaseCommand
    {
        public void Execute(string options) 
        {
      		string upperCase = 
               JSON.JsonHelper.Deserialize<string[]>(options)[0].ToUpper();
            PluginResult result;
            if (upperCase != "")
            {
                result = new PluginResult(PluginResult.Status.OK, upperCase);
            } else
            {
                result = new PluginResult(PluginResult.Status.ERROR, upperCase);
            }

            DispatchCommandResult(result);
        }
    }
}
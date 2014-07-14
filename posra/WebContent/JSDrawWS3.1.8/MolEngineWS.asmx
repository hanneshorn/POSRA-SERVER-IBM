<%@ WebService Language="C#" Class="MolEngineWS" %>

using System.Web.Services;
using System;
using Scilligence.MolEngine.Companion;
using Scilligence.MolEngine.Common;


[WebService(Namespace = "http://www.molengine.com/", Description = @"<h3>MolEngine SOAP Web Service (Version 1.0 Alpha)</h3>
MolEngine SOAP Web Service can be licensed, and deployed to enterprised environment behind firewall.<br>
Only a small set of MolEngine APIs are exposed here.  Please <a href='mailto:support@scilligence.com'>let us know</a> if you want to see other functions.<br>
For more information, please visit <a href='http://www.molengine.com' >www.molengine.com</a>.
<hr>
")]
public class MolEngineWS : Scilligence.MolEngine.Companion.SOAP
{
    protected override void Log(string q)
    {
        int count = 0;
        string ip = Context.Request.UserHostName;
        string connstr = System.Configuration.ConfigurationManager.AppSettings["molenginewsdb"];
        if (string.IsNullOrEmpty(connstr) || string.IsNullOrEmpty(q))
            return;
        
        using (Database db = DbSql.Create(connstr))
        {
            string sql = "select count(*) from MolEngineCalls where IP=" + DbSql.SqlSafe(ip) + " and Dt>dateadd(day, -1, getdate())";
            count = Scilligence.MolEngine.Companion.Utils.ToInt32(db.SelectSingleString(sql));
            if (count < 20)
            {
                Database.Row r = new Database.Row();
                r.Add("q", q.Length > 500 ? q.Substring(0, 500) + "..." : q);
                r.Add("ip", ip);
                db.Insert("MolEngineCalls", r);
            }
        }
        
        if (count >= 20)
            throw new System.Exception("With evalucation license, 20 calls can be placed in 24 hours");
    }
}
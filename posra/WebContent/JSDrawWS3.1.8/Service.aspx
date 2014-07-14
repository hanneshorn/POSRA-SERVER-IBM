<!-- ﻿
//////////////////////////////////////////////////////////////////////////////////
//
// JSDraw Web Services
// Copyright Scilligence Corporation, 2009-2012
// http://www.scilligence.com/
//
//////////////////////////////////////////////////////////////////////////////////
-->

<%@ Page Language="C#" AutoEventWireup="true" ValidateRequest="false" Inherits="Scilligence.MolEngine.Companion.JSDrawWS" %>
<%@ Import Namespace="Scilligence.MolEngine" %>
<%@ Import Namespace="Scilligence.MolEngine.Companion" %>

<script runat="server">
    // Example code to extend JSDraw.WebService
    protected override bool OnCmd(string cmd, NameValueCollection items, System.IO.TextWriter writer, out string error)
    {
        error = null;
        
        // create a command, called smiles2molfile, to convert smiles into structure
        // To test it, load smiles2molfile.htm in browser
        if (cmd == "smiles2molfile")
        {
            // input smiles
            string smiles = items["smiles"];

            Molecule m  = Molecule.ReadSMILES(smiles);

            // generate 2d Coordinate
            m.Cleanup();

            // convert aromatic mode into kekulize mode
            m.Kekulize();

            // generate molfile string
            string molfile = m.ToString(Molecule.MimetypeMol);

            writer.Write("{molfile:" + Utils.EscJsonStr(molfile) + "}");
            
            return true;
        }

        // create command, called id2image, to show chemical structure in web pages
        // To test it, load id2image.htm in browser
        if (cmd == "id2image")
        {
            string id = items["compoundid"];
            string smiles;
            using (Database db = Database.Create("sqlserver", "Data Source=server;initial catalog=CompoundDB;User Id=SA;password=PWD;"))
            {
                string sql = "select smiles from democompounds where compoundid=" + Database.SqlSafe(id);
                smiles = db.SelectSingleString(sql);
            }
            
            Molecule m = Molecule.ReadSMILES(smiles);

            // generate 2d Coordinate
            m.Cleanup();

            // convert aromatic mode into kekulize mode
            m.Kekulize();

            byte[] buf = null;
            using (System.IO.MemoryStream ms = new System.IO.MemoryStream())
            {
                m.SaveImage(ms, "png", false, false);
                buf = ms.ToArray();
            }

            Utils.HttpStream(Response, "image/png", buf, "m.png", null);
            Response.End();
        }
        
        return base.OnCmd(cmd, items, writer, out error);
    }
</script>

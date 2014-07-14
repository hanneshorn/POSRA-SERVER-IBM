<!--
//////////////////////////////////////////////////////////////////////////////////
//
// JSDraw Web Services
// Copyright (C) 2013 Scilligence Corporation
// http://www.scilligence.com/
//
//////////////////////////////////////////////////////////////////////////////////
-->

<%@ Page Language="C#" AutoEventWireup="true" ValidateRequest="false" Inherits="Scilligence.MolEngine.Companion.CompProfile" %>

<script runat="server">
    override protected Scilligence.MolEngine.Companion.MolProperty[] OnCompute(string user, string molname, string smiles, string molfile, string property, string profiler, out string error)
    {
        //return base.OnCompute(user, molfile, smiles, molfile, property, reserved, out error);
        
        error = null;
        System.Collections.Generic.List<Scilligence.MolEngine.Companion.MolProperty> list = new System.Collections.Generic.List<Scilligence.MolEngine.Companion.MolProperty>();
        switch (property)
        {
            case "pKi":
                list.Add(new Scilligence.MolEngine.Companion.MolProperty("", property, "1.0"));
                break;
            case "pKa":
                list.Add(new Scilligence.MolEngine.Companion.MolProperty("", property, "2.3"));
                break;
            case "CLogP":
                list.Add(new Scilligence.MolEngine.Companion.MolProperty("", property, "1.9"));
                break;
            case "All":
            case "*":
            case "":
            case null:
                if (profiler == "Bingo")
                {
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Assay", "version", "1.0"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Assay", "IC50", "1.23"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Availability", "Solid", "15mg"));
                }
                else if (profiler == "Action")
                {
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Assay", "IC50", "1.23"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Availability", "Solid", "15mg"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("ELN", "Exp", "EXP-001-1"));
                }
                else
                {
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Assay", "version", "1.0"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Assay", "IC50", "1.23"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("Availability", "Solid", "15mg"));
                    list.Add(new Scilligence.MolEngine.Companion.MolProperty("ELN", "Exp", "EXP-001-1"));
                }
                break;
        }
        

        return list.ToArray();
    }
</script>
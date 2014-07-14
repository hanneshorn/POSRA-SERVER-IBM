//////////////////////////////////////////////////////////////////////////////////
//
// MolEngine
// Copyright Scilligence Corporation, 2011-2012
// http://www.scilligence.com/
//
//////////////////////////////////////////////////////////////////////////////////


using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Scilligence.MolEngine;
using System.IO;

namespace MolEngine
{
    public partial class Form1 : Form
    {
        Scilligence.MolEngine.IO.SDFile sdf = null;
        MemorySearch ms = null;
        MemorySearch.Record[] searchResults = null;
        int searchIndex = 0;
        Scilligence.JSDraw.Net.JSDrawControl jsDrawControl1;

        public Form1()
        {
            // Please contact support@scilligence.com for evaluation licenses

            // set MolEngine license code
            // You can also put the license code in application config file, or web config file
            Scilligence.MolEngine.Common.Security.AddLicense(@"# Licensed to: (Evaluation)
# Product: DevSuite
# Modules: 
# SN: 2180380e-8479-45e1-baf7-5f0fb0c21a05
# Expiration Date: 2014-Jul-16
MDIwMDAzMDQwMzExMDgxMDExMDkxNDE0MTUxMTA4MDAwODA0MDQwODE1MDMxMjEwMTExMTA1MDkwMjAz
MDAwNzEwMTMwNzE1MTIxNDA5MDkxMjE0MDAwODE0MDIxNDEzMTQxMDAzMTUwNTA0MDUwMjA3MDExNDAw
MDkxNTAzMDAxMzAxMDkwNDAxMDUwNDAwMDgxMTExMDYwMTExMTAxMDAwMDExMzAzMDEwNDAxMDUwMjAx
MTMxMTA2MTUxNTAxMTMxMDAxMDUwMDEyMTQwNTA5MDMwOTAzMTMwNjEyMDUwMzA0MTAwOTE1MTIwNDA3
MDgwNDA5MDIwMTE0MTEwOTEzMTExNDAzMDAwNDA4MDYwMzE1MDMxMzEyMDkwNzA5MDIxNDA0MDYwMzA2
MDUwODE0MTExMjAxMDYwMTA2MDEwMDExMTIxMzA1MTQxMzA2MTEwMjA4MTAwMzA5MDYwNjA0MDYxMjAy
MTQxMjA5MDkwMjE0MDkwMjA3MDExNDExMDkwMDEzMDMwOTE0MDkxNTA4MDIxMzE0MDQwNTE0MDcxMDAx
MDIxNTExMDIwNjAzMDkxMzAxMDEwMTEyMDUwOTEwMDUxMTA5MDgxMjA2MDExMjEyMDQxMzA5MDkxMTEw
MDQwMTAzMDMwMTA3MTMwNDAwMDUxMTA1MTIwNDA0MDEwMzEzMDYwNzAxMDAxNTE0MDUwNTA2MDMwMjEw
MDAwNjA5MDAxMjAxMDAwNjE0MTMxNTEzMDMxNDAyMDMxMzA0MDQxMjAyMDIxNDA0MDMwMTA5MDQxMTEx
MDgxMjEyMDAwMjAyMDMwMTEzMTEwMzEyMDkwMTE0MTQxNTAyMDEwNDA1MDQwMDA0MTEwNjExMDcwODE0
MDQwOTA4MTQxMjA3MDgwNTExMTQxMzEyMTExNTEwMDcwOTEyMTUwMzA3MDQxMTAxMTIxMjA2MDEwMzAx
MDcxMDAyMTQwNDExMTExMTAzMTAxMTA2MDAwNTA0MDcwMzA1MTAwMDA5MDgwODEwMTQwMDEzMTQxMjA5
MDgwOTA5MDgxNDEzMDkwMjA2MDUwNzA4MTUxNDE0MDkwNzAxMTExMDEyMDgwNDA0MTIwNDA1MDUwMDE0
MTIwMDE0MDIxNTAyMDcwOTE0MTExNDEwMDcxNDE0MTAxMzAwMTIwMTAwMDAwMDEzMDExNTE0MDIxMDA1
MDEwOTE0MDIwMTEwMTQwMTAzMDEwNjE1MDcwMTA2MTUxMjAwMTMxNTA1MTIwNzA4MDcxMzEwMDYwODEy
MDAxMjA1MDYwNTAyMDUxMTAwMTIwODEwMTQxMDEzMDQxMjAxMTAxNTAwMTQxMjExMTMxMTA1MDIxNTA1
MDQxNTE1MDkwMDAyMTUxNDE1MDgwNjA5MDExMDAwMDYxNDE1MTAxMzEzMTUxMDE0MDcxMTA5MTEwODEw
MTIxMTA4MTExMDAxMDAwMzAxMDAwNzAzMTAwMzAyMDcxMzEzMDQwNzEyMDIxMTA4MDgxNDA4MDcwNDAw
MDMwMTE0MDkwNzA2MDkwNTA0MTUwNzAwMDIwMDEzMDkwMzA0MDYxMTEyMTUwMzEyMDUxMTE1MDkwODA2
MDkxMjA2MDUxMDA2MDkwODE0MTQxMzE0MTMxNDEwMDAwNjA0MDUwOTA5MTExMjEwMDcxMzA0MTAxNTEz
MDUxMDEzMDcwMjA0MDMwNTA3MTQxNTA5MDUwNjA4MDQxNDA1MDgwNjAwMTAwMDA3MTQwODExMTMwMTA3
MTIwMDEyMTEwMDAwMDAwODAxMTUwMTE0
");

            InitializeComponent();


            // create drawing box with JSDraw.NET
            this.jsDrawControl1 = new Scilligence.JSDraw.Net.JSDrawControl(true);
            this.jsDrawControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.jsDrawControl1.Location = new System.Drawing.Point(0, 24);
            this.jsDrawControl1.Name = "jsDrawControl1";
            this.jsDrawControl1.Size = new System.Drawing.Size(1064, 600);
            this.jsDrawControl1.TabIndex = 2;
            tabPage13.Controls.Add(this.jsDrawControl1);
            tabPage13.ResumeLayout(false);


            // initialize test files
            string path = Path.GetDirectoryName(GetType().Assembly.Location);

            convSource.Text = Path.Combine(path, "ChemDraw.cdx");
            convDest.Text = Path.Combine(path, "ChemDraw.mol");

            sdfFile.Text = Path.Combine(path, "sdf2.sdf");

            searchSdf.Text = Path.Combine(path, "sdf2.sdf");
            searchType.SelectedIndex = 0;

            rxnFile.Text = Path.Combine(path, "rxn2.rxn");

            hFile.Text = Path.Combine(path, "molwithhydrogens.mol");

            combiRxn.Text = Path.Combine(path, "GenericRxn.cdx");
            combiComp1.Text = Path.Combine(path, "component1.sdf");
            combiComp2.Text = Path.Combine(path, "component2.sdf");
        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start(linkLabel1.Text);
        }

        private void linkLabel2_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("mailto:" + linkLabel2.Text);
        }

        private void linkLabel3_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("http://www.scilligence.com/web/contact.aspx");
        }

        private void button1_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "All File|*.*|*.cdx|*.cdx|*.cdxml|*.cdxml|*.skc|*.skc|*.mol|*.mol|*.rxn|*.rxn|*.mrv|*.mrv|*.cml|*.cml|*.mol2|*.mol2|*.tgf|*.tgf|*.smi|*.smi|*.jsdraw|*.jsdraw";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    convSource.Text = dlg.FileName;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            using (SaveFileDialog dlg = new SaveFileDialog())
            {
                dlg.Filter = "*.mol|*.mol|*.cdx|*.cdx|*.cdxml|*.cdxml|*.rxn|*.rxn|*.mrv|*.mrv|*.cml|*.cml|*.mol2|*.mol2|*.tgf|*.tgf|*.smi|*.smi|*.jsdraw|*.jsdraw";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    convDest.Text = dlg.FileName;
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            // read the source file
            MolBase m = MolBase.ReadFile(convSource.Text);

            // write it to the destination file
            m.Write(convDest.Text);

            // open the converted file
            System.Diagnostics.Process.Start(convDest.Text);
        }

        private void sdfBrowse_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.sdf|*.sdf";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    sdfFile.Text = dlg.FileName;
            }
        }

        private void sdfRead_Click(object sender, EventArgs e)
        {
            // open SDF file
            sdf = new Scilligence.MolEngine.IO.SDFile(sdfFile.Text, Scilligence.MolEngine.IO.MDLDBBase.OpenMode.Read);
            sdfNext_Click(null, EventArgs.Empty);
        }

        private void sdfNext_Click(object sender, EventArgs e)
        {
            if (sdf == null)
            {
                MessageBox.Show("No SDF is read");
                return;
            }

            // read a record from the sdf file
            Dictionary<string, object> items = new Dictionary<string,object>();
            Molecule m = null;
            int id;
            if (!sdf.ReadNext(items, out m, out id))
            {
                MessageBox.Show("No more structure");
                return;
            }

            // generate structure image
            sdfPic.BackgroundImage = m.ToImage();

            // read text fields
            string ss = null;
            foreach (string s in items.Keys)
                ss += s + " = " + items[s] + "\r\n";
            sdfTxt.Text = ss;
        }

        private void button1_Click_1(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.sdf|*.sdf|*.molengine|*.molengine";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    searchSdf.Text = dlg.FileName;
            }
        }

        private void button2_Click_1(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "All File|*.*|*.cdx|*.cdx|*.cdxml|*.cdxml|*.skc|*.skc|*.mol|*.mol|*.mrv|*.mrv|*.cml|*.cml|*.mol2|*.mol2|*.tgf|*.tgf|*.smi|*.smi|*.jsdraw|*.jsdraw";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    searchQuery.Text = dlg.FileName;
            }
        }

        private void searchSearch_Click(object sender, EventArgs e)
        {
            // create a MemorySearch object for structure search
            ms = new MemorySearch();

            // load database or sdf file
            if (searchSdf.Text.EndsWith(".molengine"))
                ms.Read(searchSdf.Text);
            else if (searchSdf.Text.EndsWith(".sdf"))
                ms.AddSdf(searchSdf.Text);
            else
            {
                MessageBox.Show("Unknown file format");
                return;
            }

            // prepare query structure
            string smiles = null;
            if (searchSmiles.Text != "")
                smiles = searchSmiles.Text;
            else
            {
                Molecule m = Molecule.ReadFile(searchQuery.Text);
                smiles = m == null ? null : m.SMILES;
            }

            if (string.IsNullOrEmpty(smiles))
            {
                MessageBox.Show("No query is read");
                return;
            }

            // search type
            double cutoff = 0.6;
            MemorySearch.SearchMode mode = MemorySearch.SearchMode.Substructure;
            switch (searchType.Text)
            {
                case "Full-structure":
                    mode = MemorySearch.SearchMode.FullStructure;
                    break;
                case "Exact":
                    mode = MemorySearch.SearchMode.ExactFullStructure;
                    break;
                case "Similarity 60%":
                    mode = MemorySearch.SearchMode.Similarity;
                    cutoff = 0.6;
                    break;
                case "Similarity 90%":
                    mode = MemorySearch.SearchMode.Similarity;
                    cutoff = 0.9;
                    break;
            }

            // highlight query structures
            ms.Highlighting = Color.Red;

            // search ...
            searchResults = ms.Search(Molecule.ReadSMILES(smiles), mode, 200, cutoff);

            // browse to the first hit
            searchIndex = 0;
            searchNext_Click(null, EventArgs.Empty);
        }

        private void searchNext_Click(object sender, EventArgs e)
        {
            if (searchResults == null)
            {
                MessageBox.Show("No search results");
                return;
            }

            if (searchIndex >= searchResults.Length)
            {
                MessageBox.Show("End of records");
                return;
            }
            
            // get the current record
            MemorySearch.Record r = searchResults[searchIndex++];
            Molecule m = r.Mol.Clone();

            // show structure in kekule mode
            m.Kekulize();

            // generate structure image
            searchPic.BackgroundImage = m.ToImage();

            // read associated text field
            string ss = null;
            foreach (string s in r.items.Keys)
                ss += s + " = " + r.items[s] + "\r\n";
            searchTxt.Text = ss;
        }

        private void g2dGenerate_Click(object sender, EventArgs e)
        {
            // read smiles
            MolBase m = MolBase.ReadSMILES(g2dSmiles.Text);

            // generate 2D coordinates
            m.Cleanup();

            // generate structure image
            g2dPic.BackgroundImage = m.ToImage();
        }

        private void button3_Click_1(object sender, EventArgs e)
        {
            // read smiles
            MolBase m = MolBase.ReadSMILES(hashSmiles.Text);

            // generate 2D coordinates
            m.Cleanup();

            // generate structure hash key, considering stereochemistry and tautomer
            hashKey.Text = m.GetCanonicalCode(true).GUID.ToString().ToUpper();

            // generate structure hash key, not considering stereochemistry and tautomer
            hashRelax.Text = m.GetCanonicalCode(false).GUID.ToString().ToUpper();

            // generate structure images
            hashPic.BackgroundImage = m.ToImage();
        }

        private void button4_Click(object sender, EventArgs e)
        {
            // read smiles
            Molecule m = Molecule.ReadSMILES(hashSmiles.Text);

            // compute molecule properties
            string ss = null;
            ss += "MF = " + m.Formula + "\r\n";
            ss += "MF (HTML) = " + m.FormulaHtml + "\r\n";
            ss += "MW = " + m.MolWeight + "\r\n";
            ss += "Exact Mass = " + m.ExactMass + "\r\n";
            ss += "Formal Charges = " + m.FormalCharge() + "\r\n";
            ss += "t-PSA = " + m.PSA + "\r\n";
            ss += "Number of Rotate Bonds = " + m.NumberOfRotableBonds() + "\r\n";
            ss += "Number of Hydrogen Acceptors = " + m.NumberOfHydrogenAcceptors() + "\r\n";
            ss += "Number of Hydrogen Donors = " + m.NumberOfHydrogenDonors() + "\r\n";
            ss += "Atom Count = " + m.Atoms.Count + "\r\n";
            ss += "Stereo Centers = " + m.Chiralities.Atoms.Count + "\r\n";
            ss += "SMILES = " + m.SMILES + "\r\n";
            ss += "Hash Key (Canonical Code) = " + m.GetCanonicalCode(true).GUID.ToString().ToUpper() + "\r\n";
            ss += "Hash Key (No Stereochemistry) = " + m.GetCanonicalCode(false).GUID.ToString().ToUpper() + "\r\n";
            ss += "Finerprints = " + m.FingerprintsAsString + "\r\n";
            ss += "Structure Key = " + m.StructureKeysAsString + "\r\n";

            propTxt.Text = ss;
        }

        private void button5_Click(object sender, EventArgs e)
        {
            // read smiles
            MolBase m = MolBase.ReadSMILES(kekSmiles.Text);

            // generate 2D coordinates
            m.Cleanup();

            // kekulize
            m.Kekulize();

            // generating structure image
            kekPic1.BackgroundImage = m.ToImage();
        }

        private void button6_Click(object sender, EventArgs e)
        {
            // read smiles
            MolBase m = MolBase.ReadSMILES(kekSmiles.Text);

            // generating 2D coordinates
            m.Cleanup();

            // Dekekulize
            m.Dekekulize();

            // generate structure image
            kekPic2.BackgroundImage = m.ToImage();
        }

        private void button8_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.mol|*.mol|*.cdx|*.cdx|*.cdxml|*.cdxml|*.mrv|*.mrv|*.cml|*.cml|*.mol2|*.mol2|*.tgf|*.tgf|*.smi|*.smi|*.jsdraw|*.jsdraw";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    hFile.Text = dlg.FileName;
            }
        }

        private void button7_Click(object sender, EventArgs e)
        {
            // read structure
            MolBase m = MolBase.ReadFile(hFile.Text);
            
            // generate structure image before removing hydrogens
            hPic1.BackgroundImage = m.ToImage();

            // remove hydrogens
            m.RemoveHydrogens();

            // generate structure image after removing hydrogens
            hPic2.BackgroundImage = m.ToImage();
        }

        private void button9_Click(object sender, EventArgs e)
        {
            // read smiles
            MolBase m = MolBase.ReadSMILES(normSmiles.Text);

            // generate 2D coordinate
            m.Cleanup();

            // generate structure image before normalization
            normPic1.BackgroundImage = m.ToImage();

            // create normalization object
            Normalization norm = new Normalization();
            norm.Neutralization = true;
            norm.SaltStripping = true;
            norm.Kekulize = true;
            norm.RemovingHydrogens = true;
            norm.Cleanup = true;
            norm.BreakMetalBonds = true;
            norm.CanonicalizeTautomer = true;

            // normalize a structure
            m = norm.Process(m);

            // generate structure image after normalization
            normPic2.BackgroundImage = m.ToImage();
        }

        private void button10_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.rxn|*.rxn|*.cdx|*.cdx|*.cdxml|*.cdxml|*.skc|*.skc|*.mrv|*.mrv|*.tgf|*.tgf|*.jsdraw|*.jsdraw";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    rxnFile.Text = dlg.FileName;
            }
        }

        private void button11_Click(object sender, EventArgs e)
        {
            // read reaction
            Reaction r = Reaction.ReadFile(rxnFile.Text);
            if (r == null)
            {
                MessageBox.Show("Invalid Reaction");
                return;
            }

            // loop through reactants
            int i = 0;
            string ss = null;
            foreach (Molecule m in r.Reactants)
                ss += (++i) + ". " + m.Formula + ", " + m.SMILES + "\r\n";
            rxnReactants.Text = ss;

            // loop through products
            i = 0;
            ss = null;
            foreach (Molecule m in r.Products)
                ss += (++i) + ". " + m.Formula + ", " + m.SMILES + "\r\n";
            rxnProducts.Text = ss;

            // generate 2D coordinates and layour reaction components
            r.Cleanup();

            // generate reaction image
            rxnPic.BackgroundImage = r.ToImage();
        }

        private void button12_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.rxn|*.rxn|*.cdx|*.cdx|*.cdxml|*.cdxml|*.skc|*.skc|*.mrv|*.mrv|*.tgf|*.tgf|*.jsdraw|*.jsdraw";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    combiRxn.Text = dlg.FileName;
            }
        }

        private void button13_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.sdf|*.sdf";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    combiComp1.Text = dlg.FileName;
            }
        }

        private void button14_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.sdf|*.sdf";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    combiComp2.Text = dlg.FileName;
            }
        }

        private void button15_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog dlg = new OpenFileDialog())
            {
                dlg.Filter = "*.sdf|*.sdf";
                if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                    combiComp3.Text = dlg.FileName;
            }
        }

        private void button16_Click(object sender, EventArgs e)
        {
            // clear reaction table
            combTable.Rows.Clear();

            // prepare the generic reaction
            Reaction rxn = null;
            MolBase s = MolBase.ReadFile(combiRxn.Text);
            if (s as Reaction != null)
                rxn = (Reaction)s;
            else if (s as Sketch != null)
                rxn = ((Sketch)s).AsReaction();
            if (rxn == null)
            {
                MessageBox.Show("Not a valid reaction");
                return;
            }

            // create a combi oject
            Combi combi = new Combi(rxn);

            // add reagents for each R-group reactant component
            int i = 0;
            foreach (Combi.Component c in combi.Components)
            {
                ++i;
                string f = null;
                if (i == 1)
                    f = combiComp1.Text;
                else if (i == 2)
                    f = combiComp2.Text;
                else if (i == 3)
                    f = combiComp3.Text;
                else
                    break;

                using (Scilligence.MolEngine.IO.SDFile sdf = new Scilligence.MolEngine.IO.SDFile(f, Scilligence.MolEngine.IO.MDLDBBase.OpenMode.Read))
                {
                    Scilligence.MolEngine.IO.SDFile.Record r2;
                    while ((r2 = sdf.ReadNext()) != null)
                    {
                        if (r2.m as Molecule != null)
                            c.AddReagent((Molecule)r2.m);
                    }
                }
            }

            // enumerate
            int ri = 0;
            Reaction r;
            // begin to enumerate
            combi.ResetEnum();

            // enumerate one
            while ((r = combi.EnumNext()) != null)
            {
                // display the enumerated reaction
                DataGridViewRow row = new DataGridViewRow();
                row.Height = 200;
                row.Cells.Add(new DataGridViewImageCell());

                MolDrawing md = new MolDrawing(r, new System.Drawing.Rectangle(0, 0, combTable.Columns[0].Width, row.Height), 10);
                row.Cells[0].Value = md.ToImage();
                combTable.Rows.Add(row);
                row.HeaderCell.Value = (++ri).ToString();
            }
        }

        private void button6_Click_1(object sender, EventArgs e)
        {
            // read smiles
            MolBase m = MolBase.ReadSMILES(kekSmiles.Text);

            // generate 2D coordinates
            m.Cleanup();

            // dekekulize
            m.Dekekulize();

            // generate structure images
            kekPic2.BackgroundImage = m.ToImage();
        }

        private void drawer1_Load(object sender, EventArgs e)
        {

        }

        string structure;
        private void button17_Click(object sender, EventArgs e)
        {
            Scilligence.JSDraw.Net.PopupEditor pe = Scilligence.JSDraw.Net.PopupEditor.Show(structure, this);
            if (pe.Result != System.Windows.Forms.DialogResult.OK)
                return;

            structure = pe.Html;
            Molecule m = Molecule.Read(structure);
            string smiles = m.ToString("smiles");
            string molfile = m.ToString("molfile");
            pictureBox1.Image = m.ToImage(pictureBox1.ClientSize.Width, pictureBox1.ClientSize.Height, 10);
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.jsDrawControl1.SetXml(@"<div version='1.3' class='JSDraw' app='JSDraw V2.7.1' style='width:772px;height:326px;'>
  <a i='1' e='C' p='7.831 -9.820' clr='#FFFFFF'/>
  <a i='2' e='C' p='6.480 -9.040' clr='#FFFFFF'/>
  <a i='3' e='C' p='6.480 -7.480' clr='#FFFFFF'/>
  <a i='4' e='C' p='9.182 -9.040' clr='#FFFFFF'/>
  <a i='5' e='C' p='9.182 -7.480' clr='#FFFFFF'/>
  <a i='6' e='C' p='7.831 -6.700' clr='#FFFFFF'/>
  <a i='7' e='C' p='10.533 -9.820' clr='#FFFFFF'/>
  <a i='8' e='C' p='11.884 -9.040' clr='#FFFFFF'/>
  <a i='9' e='C' p='11.884 -7.480' clr='#FFFFFF'/>
  <a i='10' e='C' p='10.533 -6.700' clr='#FFFFFF'/>
  <a i='11' e='C' p='13.235 -9.820' clr='#FFFFFF'/>
  <a i='12' e='C' p='14.586 -9.040' clr='#FFFFFF'/>
  <a i='13' e='C' p='14.586 -7.480' clr='#FFFFFF'/>
  <a i='14' e='C' p='13.235 -6.700' clr='#FFFFFF'/>
  <a i='15' e='O' p='7.831 -11.380' clr='#FFFFFF'/>
  <a i='16' e='C' p='7.831 -5.140' alias='COOH' clr='#FFFFFF'>
    <superatom>
      <div>
        <a i='1' e='C' p='0.322 -0.231' apo='1'/>
        <a i='2' e='O' p='0.392 -0.191'/>
        <a i='3' e='O' p='0.252 -0.191'/>
        <b i='4' a1='1' a2='2' t='2'/>
        <b i='5' a1='1' a2='3' t='1'/>
      </div>
    </superatom>
  </a>
  <a i='17' e='C' p='28.067 -9.603' clr='#FFFFFF'/>
  <a i='18' e='C' p='26.716 -8.823' clr='#FFFFFF'/>
  <a i='19' e='C' p='26.716 -7.263' clr='#FFFFFF'/>
  <a i='20' e='C' p='29.418 -8.823' clr='#FFFFFF'/>
  <a i='21' e='C' p='29.418 -7.263' clr='#FFFFFF'/>
  <a i='22' e='C' p='28.067 -6.483' clr='#FFFFFF'/>
  <a i='23' e='C' p='30.769 -9.603' clr='#FFFFFF'/>
  <a i='24' e='C' p='32.120 -8.823' clr='#FFFFFF'/>
  <a i='25' e='C' p='32.120 -7.263' clr='#FFFFFF'/>
  <a i='26' e='C' p='30.769 -6.483' clr='#FFFFFF'/>
  <a i='27' e='C' p='33.471 -9.603' clr='#FFFFFF'/>
  <a i='28' e='C' p='34.822 -8.823' clr='#FFFFFF'/>
  <a i='29' e='C' p='34.822 -7.263' clr='#FFFFFF'/>
  <a i='30' e='C' p='33.471 -6.483' clr='#FFFFFF'/>
  <a i='31' e='O' p='28.067 -11.163' clr='#FFFFFF'/>
  <a i='32' e='C' p='28.067 -4.923' alias='COOH' clr='#FFFFFF'>
    <superatom>
      <div>
        <a i='1' e='C' p='0.322 -0.231' apo='1'/>
        <a i='2' e='O' p='0.392 -0.191'/>
        <a i='3' e='O' p='0.252 -0.191'/>
        <b i='4' a1='1' a2='2' t='2'/>
        <b i='5' a1='1' a2='3' t='1'/>
      </div>
    </superatom>
  </a>
  <a i='33' e='C' p='26.716 -11.943' clr='#FFFFFF'/>
  <a i='34' e='C' p='25.365 -11.163' clr='#FFFFFF'/>
  <a i='35' e='C' p='24.014 -11.943' clr='#FFFFFF'/>
  <b i='36' a1='1' a2='2' t='1' clr='#FFFFFF'/>
  <b i='37' a1='2' a2='3' t='2' clr='#FFFFFF'/>
  <b i='38' a1='1' a2='4' t='2' clr='#FFFFFF'/>
  <b i='39' a1='4' a2='5' t='1' clr='#FFFFFF'/>
  <b i='40' a1='5' a2='6' t='2' clr='#FFFFFF'/>
  <b i='41' a1='6' a2='3' t='1' clr='#FFFFFF'/>
  <b i='42' a1='7' a2='4' t='1' clr='#FFFFFF'/>
  <b i='43' a1='7' a2='8' t='2' clr='#FFFFFF'/>
  <b i='44' a1='8' a2='9' t='1' clr='#FFFFFF'/>
  <b i='45' a1='9' a2='10' t='2' clr='#FFFFFF'/>
  <b i='46' a1='10' a2='5' t='1' clr='#FFFFFF'/>
  <b i='47' a1='11' a2='8' t='1' clr='#FFFFFF'/>
  <b i='48' a1='11' a2='12' t='2' clr='#FFFFFF'/>
  <b i='49' a1='12' a2='13' t='1' clr='#FFFFFF'/>
  <b i='50' a1='13' a2='14' t='2' clr='#FFFFFF'/>
  <b i='51' a1='14' a2='9' t='1' clr='#FFFFFF'/>
  <b i='52' a1='1' a2='15' t='1' clr='#FFFFFF'/>
  <b i='53' a1='6' a2='16' t='1' clr='#FFFFFF'/>
  <b i='54' a1='17' a2='18' t='1' clr='#FFFFFF'/>
  <b i='55' a1='18' a2='19' t='2' clr='#FFFFFF'/>
  <b i='56' a1='17' a2='20' t='2' clr='#FFFFFF'/>
  <b i='57' a1='20' a2='21' t='1' clr='#FFFFFF'/>
  <b i='58' a1='21' a2='22' t='2' clr='#FFFFFF'/>
  <b i='59' a1='22' a2='19' t='1' clr='#FFFFFF'/>
  <b i='60' a1='23' a2='20' t='1' clr='#FFFFFF'/>
  <b i='61' a1='23' a2='24' t='2' clr='#FFFFFF'/>
  <b i='62' a1='24' a2='25' t='1' clr='#FFFFFF'/>
  <b i='63' a1='25' a2='26' t='2' clr='#FFFFFF'/>
  <b i='64' a1='26' a2='21' t='1' clr='#FFFFFF'/>
  <b i='65' a1='27' a2='24' t='1' clr='#FFFFFF'/>
  <b i='66' a1='27' a2='28' t='2' clr='#FFFFFF'/>
  <b i='67' a1='28' a2='29' t='1' clr='#FFFFFF'/>
  <b i='68' a1='29' a2='30' t='2' clr='#FFFFFF'/>
  <b i='69' a1='30' a2='25' t='1' clr='#FFFFFF'/>
  <b i='70' a1='17' a2='31' t='1' clr='#FFFFFF'/>
  <b i='71' a1='22' a2='32' t='1' clr='#FFFFFF'/>
  <b i='72' a1='31' a2='33' t='1' clr='#FFFFFF'/>
  <b i='73' a1='33' a2='34' t='1' clr='#FFFFFF'/>
  <b i='74' a1='34' a2='35' t='1' clr='#FFFFFF'/>
  <i i='75' x='SHAPE' type='rectangle' fill='linear' p='3.194 -14.196 33.757 11.440' clr='#000000'></i>
  <i i='76' x='TEXT' p='29.410 -13.139 6.292 0.936' clr='#00FFFF' anchors=''>Draw using JSDraw</i>
  <i i='77' x='ARROW' p1='16.411 -8.390' p2='22.479 -8.390' clr='#0000FF'></i>
  <i i='78' x='TEXT' p='18.361 -7.939 2.080 0.936' clr='#0000FF' anchors=''>250°C</i>
  <i i='79' x='TEXT' p='19.184 -9.716 0.468 0.936' clr='#0000FF' anchors=''>Δ</i>
  <i i='80' x='CURVE' p1='9.131 -11.856' p2='27.330 -10.773' p1a='20.372 -15.404' p2a='20.449 -6.490' clr='#FFFFFF'></i>
</div>");
        }
    }
}

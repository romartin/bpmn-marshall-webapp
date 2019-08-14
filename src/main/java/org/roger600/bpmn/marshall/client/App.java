/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.roger600.bpmn.marshall.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import org.eclipse.bpmn2.DocumentRoot;
import org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling;

public class App implements EntryPoint {

    public void onModuleLoad() {
        final Button unmarshallButton = new Button("Unmarshall");
        final Button marshallButton = new Button("Marshall");
        final Button testButton = new Button("Test");
        testButton.setVisible(false);
        final TextArea codeField = new TextArea();
        final Label textToServerLabel = new Label();
        final HTML serverResponseLabel = new HTML();
        //codeField.setText(TEXT);
        codeField.setText(SIMPLE_PROCESS);
        codeField.setWidth("90%");
        codeField.setVisibleLines(20);

        final Label errorLabel = new Label();

        // We can add style names to widgets
        unmarshallButton.addStyleName("sendButton");
        marshallButton.addStyleName("sendButton");
        testButton.addStyleName("sendButton");

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(unmarshallButton);
        buttons.add(marshallButton);
        buttons.add(testButton);

        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get("nameFieldContainer").add(codeField);
        RootPanel.get("sendButtonContainer").add(buttons);
        RootPanel.get("errorLabelContainer").add(errorLabel);
        RootPanel.get("outputContainer").add(textToServerLabel);
        RootPanel.get("errorContainer").add(serverResponseLabel);

        // Focus the cursor on the name field when the app loads
        codeField.setFocus(true);
        codeField.selectAll();

        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler,
                                   KeyUpHandler {

            private DocumentRoot lastRoot;

            /**
             * Fired when the user types in the nameField.
             */
            public void onKeyUp(KeyUpEvent event) {
                /*if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    doUnmarshall();
                }*/
            }

            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onClick(ClickEvent event) {
                if (null == lastRoot) {
                    doUnmarshall();
                } else {
                    doMarshall();
                }
            }

            private void doMarshall() {
                GWT.log("Doing marshall");
                String raw = Bpmn2Marshalling.marshall(lastRoot);
                setResultText(raw);
                setLastRoot(null);
            }

            /**
             * Send the name from the nameField to the server and wait for a response.
             */
            private void doUnmarshall() {
                GWT.log("Doing Unmarshall");
                // First, we validate the input.
                errorLabel.setText("");
                String text = codeField.getText();
                // Unmarshall.
                DocumentRoot result = Bpmn2Marshalling.unmarshall(text);
                setLastRoot(result);
                String resultRaw = Bpmn2Marshalling.testString(lastRoot);
                setResultText(resultRaw);
            }

            private void setLastRoot(DocumentRoot lastRoot) {
                this.lastRoot = lastRoot;
                marshallButton.setEnabled(null != lastRoot);
            }

            private void setResultText(String resultRaw) {
                GWT.log("*** RESULT START ***");
                GWT.log(resultRaw);
                GWT.log("*** RESULT END ***");
                serverResponseLabel.addStyleName("serverResponseLabelError");
                textToServerLabel.setText(resultRaw);
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        unmarshallButton.addClickHandler(handler);
        marshallButton.addClickHandler(handler);
        marshallButton.setEnabled(false);
        testButton.addClickHandler(event -> onTestButtonClick());
        codeField.addKeyUpHandler(handler);
    }

    private void onTestButtonClick() {
        // parseSomeXml();
        String wildcards = "##other";
        for (String wildcard : wildcards.split(",")) {
            GWT.log("WILDCARD = " + wildcard);
        }
    }

    private void parseSomeXml() {

        Document document = XMLParser.createDocument();

        Element definitions = document.createElement("bpmn2:definitions");
        document.appendChild(definitions);

        Node itemDefinition1 = document.createElement("bpmn2:itemDefinition");
        itemDefinition1.setNodeValue("itemDefinition1_value");
        definitions.appendChild(itemDefinition1);

        Element itemDefinition2 = document.createElement("bpmn2:itemDefinition");
        itemDefinition2.setAttribute("id", "itemDefinition2_id");
        definitions.appendChild(itemDefinition2);

        String raw = document.toString();
        GWT.log(raw);
    }

    private static final String SIMPLE_PROCESS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:drools=\"http://www.jboss.org/drools\" id=\"_GUUj8KUCEemjtN0xRqbezg\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd http://www.omg.org/spec/DD/20100524/DC DC.xsd http://www.omg.org/spec/DD/20100524/DI DI.xsd \" exporter=\"jBPM Process Modeler\" exporterVersion=\"2.0\" targetNamespace=\"http://www.omg.org/bpmn20\">\n" +
            "  <bpmn2:process id=\"test.process1\" drools:packageName=\"com.myspace.test\" drools:version=\"1.0\" name=\"process1\" isExecutable=\"true\">\n" +
            "    <bpmn2:startEvent id=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\"/>\n" +
            "  </bpmn2:process>\n" +
            "  <bpmndi:BPMNDiagram id=\"_GUUj8aUCEemjtN0xRqbezg\">\n" +
            "    <bpmndi:BPMNPlane id=\"_GUUj8qUCEemjtN0xRqbezg\" bpmnElement=\"test.process1\">\n" +
            "      <bpmndi:BPMNShape id=\"shape__B801DDDE-29E9-41C2-BF36-0045EA55F573\" bpmnElement=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\">\n" +
            "        <dc:Bounds height=\"56.0\" width=\"56.0\" x=\"100.0\" y=\"100.0\"/>\n" +
            "      </bpmndi:BPMNShape>\n" +
            "    </bpmndi:BPMNPlane>\n" +
            "  </bpmndi:BPMNDiagram>\n" +
            "  <bpmn2:relationship id=\"_GUUj86UCEemjtN0xRqbezg\" type=\"BPSimData\">\n" +
            "    <bpmn2:extensionElements>\n" +
            "      <bpsim:BPSimData>\n" +
            "        <bpsim:Scenario xsi:type=\"bpsim:Scenario\" id=\"default\" name=\"Simulationscenario\">\n" +
            "          <bpsim:ScenarioParameters xsi:type=\"bpsim:ScenarioParameters\"/>\n" +
            "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\" id=\"_GUUj9KUCEemjtN0xRqbezg\">\n" +
            "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
            "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
            "                <bpsim:NormalDistribution mean=\"0.0\" standardDeviation=\"0.0\"/>\n" +
            "              </bpsim:ProcessingTime>\n" +
            "            </bpsim:TimeParameters>\n" +
            "          </bpsim:ElementParameters>\n" +
            "        </bpsim:Scenario>\n" +
            "      </bpsim:BPSimData>\n" +
            "    </bpmn2:extensionElements>\n" +
            "    <bpmn2:source>_GUUj8KUCEemjtN0xRqbezg</bpmn2:source>\n" +
            "    <bpmn2:target>_GUUj8KUCEemjtN0xRqbezg</bpmn2:target>\n" +
            "  </bpmn2:relationship>\n" +
            "</bpmn2:definitions>";

    private static final String TEXT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:drools=\"http://www.jboss.org/drools\" id=\"_k5OowXlcEeiHSaC1r8ADkg\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd http://www.omg.org/spec/DD/20100524/DC DC.xsd http://www.omg.org/spec/DD/20100524/DI DI.xsd \" targetNamespace=\"http://www.omg.org/bpmn20\">\n" +
                    "  <bpmn2:itemDefinition id=\"__-.Item\" structureRef=\"Float\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_SkippableInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_PriorityInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_CommentInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_DescriptionInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_CreatedByInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_TaskNameInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_GroupIdInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_BodyInputXItem\" structureRef=\"String\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_asdasdaInputXItem\" structureRef=\"Object\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_FromInputXItem\" structureRef=\"String\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_SubjectInputXItem\" structureRef=\"String\"/>\n" +
                    "  <bpmn2:itemDefinition id=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_ToInputXItem\" structureRef=\"String\"/>\n" +
                    "  <bpmn2:process id=\"controlpoints.flaffo\" drools:packageName=\"com.myteam.controlpoints\" drools:version=\"1.0\" drools:adHoc=\"false\" name=\"flaffo\" isExecutable=\"true\">\n" +
                    "    <bpmn2:documentation id=\"_k5P24HlcEeiHSaC1r8ADkg\"><![CDATA[]]></bpmn2:documentation>\n" +
                    "    <bpmn2:extensionElements>\n" +
                    "      <drools:metaData name=\"customDescription\">\n" +
                    "        <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                    "      </drools:metaData>\n" +
                    "    </bpmn2:extensionElements>\n" +
                    "    <bpmn2:property id=\"var_controlpoints.flaffo__-.\" itemSubjectRef=\"__-.Item\" name=\"_-.\"/>\n" +
                    "    <bpmn2:userTask id=\"_64110518-0C20-4648-8C38-1EB22D08FFD2\" name=\"~`!@#$%^&amp;*()_+=-{}|][:&quot;;&apos;?&gt;&lt;,./\">\n" +
                    "      <bpmn2:documentation id=\"_k5P24XlcEeiHSaC1r8ADkg\"><![CDATA[]]></bpmn2:documentation>\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"elementname\">\n" +
                    "          <drools:metaValue><![CDATA[~`!@#$%^&*()_+=-{}|][:\";'?><,./ ]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"customAsync\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"customAutoStart\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:incoming>_D52F943C-450D-415A-AFC1-53D7887F7779</bpmn2:incoming>\n" +
                    "      <bpmn2:outgoing>_F1FAC58C-37B0-4ED7-B423-3CCD174261B9</bpmn2:outgoing>\n" +
                    "      <bpmn2:ioSpecification id=\"_k5RFAHlcEeiHSaC1r8ADkg\">\n" +
                    "        <bpmn2:dataInput id=\"_64110518-0C20-4648-8C38-1EB22D08FFD2_TaskNameInputX\" drools:dtype=\"Object\" itemSubjectRef=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_TaskNameInputXItem\" name=\"TaskName\"/>\n" +
                    "        <bpmn2:dataInput id=\"_64110518-0C20-4648-8C38-1EB22D08FFD2_SkippableInputX\" drools:dtype=\"Object\" itemSubjectRef=\"__64110518-0C20-4648-8C38-1EB22D08FFD2_SkippableInputXItem\" name=\"Skippable\"/>\n" +
                    "      </bpmn2:ioSpecification>\n" +
                    "      <bpmn2:dataInputAssociation id=\"_k5RFAXlcEeiHSaC1r8ADkg\">\n" +
                    "        <bpmn2:targetRef>_64110518-0C20-4648-8C38-1EB22D08FFD2_TaskNameInputX</bpmn2:targetRef>\n" +
                    "        <bpmn2:assignment id=\"_k5RFAnlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5RFA3lcEeiHSaC1r8ADkg\"><![CDATA[Task]]></bpmn2:from>\n" +
                    "          <bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5RFBHlcEeiHSaC1r8ADkg\">_64110518-0C20-4648-8C38-1EB22D08FFD2_TaskNameInputX</bpmn2:to>\n" +
                    "        </bpmn2:assignment>\n" +
                    "      </bpmn2:dataInputAssociation>\n" +
                    "      <bpmn2:dataInputAssociation id=\"_k5RFBXlcEeiHSaC1r8ADkg\">\n" +
                    "        <bpmn2:targetRef>_64110518-0C20-4648-8C38-1EB22D08FFD2_SkippableInputX</bpmn2:targetRef>\n" +
                    "        <bpmn2:assignment id=\"_k5RFBnlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5RFB3lcEeiHSaC1r8ADkg\"><![CDATA[false]]></bpmn2:from>\n" +
                    "          <bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5RFCHlcEeiHSaC1r8ADkg\">_64110518-0C20-4648-8C38-1EB22D08FFD2_SkippableInputX</bpmn2:to>\n" +
                    "        </bpmn2:assignment>\n" +
                    "      </bpmn2:dataInputAssociation>\n" +
                    "    </bpmn2:userTask>\n" +
                    "    <bpmn2:endEvent id=\"_D1B6E315-4A1C-4057-8255-209AE84B9650\" name=\"\">\n" +
                    "      <bpmn2:documentation id=\"_k5RsEHlcEeiHSaC1r8ADkg\"><![CDATA[]]></bpmn2:documentation>\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"elementname\">\n" +
                    "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:incoming>_957A6475-0F28-4CFB-BA40-3AD69692BD8D</bpmn2:incoming>\n" +
                    "    </bpmn2:endEvent>\n" +
                    "    <bpmn2:task id=\"_259F0683-FF03-45C6-AB8D-798301E2F41D\" drools:taskName=\"Email\" name=\"Email\">\n" +
                    "      <bpmn2:documentation id=\"_k5RsEXlcEeiHSaC1r8ADkg\"><![CDATA[]]></bpmn2:documentation>\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"elementname\">\n" +
                    "          <drools:metaValue><![CDATA[Email]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"customAsync\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"customAutoStart\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:incoming>_F1FAC58C-37B0-4ED7-B423-3CCD174261B9</bpmn2:incoming>\n" +
                    "      <bpmn2:outgoing>_957A6475-0F28-4CFB-BA40-3AD69692BD8D</bpmn2:outgoing>\n" +
                    "      <bpmn2:ioSpecification id=\"_k5RsEnlcEeiHSaC1r8ADkg\">\n" +
                    "        <bpmn2:dataInput id=\"_259F0683-FF03-45C6-AB8D-798301E2F41D_BodyInputX\" drools:dtype=\"String\" itemSubjectRef=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_BodyInputXItem\" name=\"Body\"/>\n" +
                    "        <bpmn2:dataInput id=\"_259F0683-FF03-45C6-AB8D-798301E2F41D_asdasdaInputX\" drools:dtype=\"Object\" itemSubjectRef=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_asdasdaInputXItem\" name=\"asdasda\"/>\n" +
                    "        <bpmn2:dataInput id=\"_259F0683-FF03-45C6-AB8D-798301E2F41D_FromInputX\" drools:dtype=\"String\" itemSubjectRef=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_FromInputXItem\" name=\"From\"/>\n" +
                    "        <bpmn2:dataInput id=\"_259F0683-FF03-45C6-AB8D-798301E2F41D_SubjectInputX\" drools:dtype=\"String\" itemSubjectRef=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_SubjectInputXItem\" name=\"Subject\"/>\n" +
                    "        <bpmn2:dataInput id=\"_259F0683-FF03-45C6-AB8D-798301E2F41D_ToInputX\" drools:dtype=\"String\" itemSubjectRef=\"__259F0683-FF03-45C6-AB8D-798301E2F41D_ToInputXItem\" name=\"To\"/>\n" +
                    "        <bpmn2:inputSet id=\"_k5RsE3lcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:dataInputRefs>_259F0683-FF03-45C6-AB8D-798301E2F41D_BodyInputX</bpmn2:dataInputRefs>\n" +
                    "        </bpmn2:inputSet>\n" +
                    "        <bpmn2:inputSet id=\"_k5STIHlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:dataInputRefs>_259F0683-FF03-45C6-AB8D-798301E2F41D_asdasdaInputX</bpmn2:dataInputRefs>\n" +
                    "        </bpmn2:inputSet>\n" +
                    "        <bpmn2:inputSet id=\"_k5STIXlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:dataInputRefs>_259F0683-FF03-45C6-AB8D-798301E2F41D_FromInputX</bpmn2:dataInputRefs>\n" +
                    "        </bpmn2:inputSet>\n" +
                    "        <bpmn2:inputSet id=\"_k5STInlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:dataInputRefs>_259F0683-FF03-45C6-AB8D-798301E2F41D_SubjectInputX</bpmn2:dataInputRefs>\n" +
                    "        </bpmn2:inputSet>\n" +
                    "        <bpmn2:inputSet id=\"_k5STI3lcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:dataInputRefs>_259F0683-FF03-45C6-AB8D-798301E2F41D_ToInputX</bpmn2:dataInputRefs>\n" +
                    "        </bpmn2:inputSet>\n" +
                    "      </bpmn2:ioSpecification>\n" +
                    "      <bpmn2:dataInputAssociation id=\"_k5STJHlcEeiHSaC1r8ADkg\">\n" +
                    "        <bpmn2:targetRef>_259F0683-FF03-45C6-AB8D-798301E2F41D_BodyInputX</bpmn2:targetRef>\n" +
                    "        <bpmn2:assignment id=\"_k5STJXlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STJnlcEeiHSaC1r8ADkg\">~%60!%40%23%24%25%5E%26*()_%2B%3D-%7B%7D%7C%5D%5B%3A%22%3B'%3F%3E%3C%2C.%2F+</bpmn2:from>\n" +
                    "          <bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STJ3lcEeiHSaC1r8ADkg\">_259F0683-FF03-45C6-AB8D-798301E2F41D_BodyInputX</bpmn2:to>\n" +
                    "        </bpmn2:assignment>\n" +
                    "      </bpmn2:dataInputAssociation>\n" +
                    "      <bpmn2:dataInputAssociation id=\"_k5STKHlcEeiHSaC1r8ADkg\">\n" +
                    "        <bpmn2:targetRef>_259F0683-FF03-45C6-AB8D-798301E2F41D_asdasdaInputX</bpmn2:targetRef>\n" +
                    "        <bpmn2:assignment id=\"_k5STKXlcEeiHSaC1r8ADkg\">\n" +
                    "          <bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STKnlcEeiHSaC1r8ADkg\">~%60!%40%23%24%25%5E%26*()_%2B%3D-%7B%7D%7C%5D%5B%3A%22%3B'%3F%3E%3C%2C.%2F+</bpmn2:from>\n" +
                    "          <bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STK3lcEeiHSaC1r8ADkg\">_259F0683-FF03-45C6-AB8D-798301E2F41D_asdasdaInputX</bpmn2:to>\n" +
                    "        </bpmn2:assignment>\n" +
                    "      </bpmn2:dataInputAssociation>\n" +
                    "    </bpmn2:task>\n" +
                    "    <bpmn2:startEvent id=\"_3201662B-28C8-48AF-AE4A-6AFF92C99A67\" name=\"\">\n" +
                    "      <bpmn2:documentation id=\"_k5STLHlcEeiHSaC1r8ADkg\"><![CDATA[]]></bpmn2:documentation>\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"elementname\">\n" +
                    "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:outgoing>_D52F943C-450D-415A-AFC1-53D7887F7779</bpmn2:outgoing>\n" +
                    "    </bpmn2:startEvent>\n" +
                    "    <bpmn2:sequenceFlow id=\"_D52F943C-450D-415A-AFC1-53D7887F7779\" name=\"\" sourceRef=\"_3201662B-28C8-48AF-AE4A-6AFF92C99A67\" targetRef=\"_64110518-0C20-4648-8C38-1EB22D08FFD2\">\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"isAutoConnection.source\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"isAutoConnection.target\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:conditionExpression xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STLXlcEeiHSaC1r8ADkg\" language=\"http://www.java.com/java\"><![CDATA[]]></bpmn2:conditionExpression>\n" +
                    "    </bpmn2:sequenceFlow>\n" +
                    "    <bpmn2:sequenceFlow id=\"_F1FAC58C-37B0-4ED7-B423-3CCD174261B9\" name=\"\" sourceRef=\"_64110518-0C20-4648-8C38-1EB22D08FFD2\" targetRef=\"_259F0683-FF03-45C6-AB8D-798301E2F41D\">\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"isAutoConnection.source\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"isAutoConnection.target\">\n" +
                    "          <drools:metaValue><![CDATA[true]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:conditionExpression xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STLnlcEeiHSaC1r8ADkg\" language=\"http://www.java.com/java\"><![CDATA[]]></bpmn2:conditionExpression>\n" +
                    "    </bpmn2:sequenceFlow>\n" +
                    "    <bpmn2:sequenceFlow id=\"_957A6475-0F28-4CFB-BA40-3AD69692BD8D\" name=\"\" sourceRef=\"_259F0683-FF03-45C6-AB8D-798301E2F41D\" targetRef=\"_D1B6E315-4A1C-4057-8255-209AE84B9650\">\n" +
                    "      <bpmn2:extensionElements>\n" +
                    "        <drools:metaData name=\"isAutoConnection.source\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "        <drools:metaData name=\"isAutoConnection.target\">\n" +
                    "          <drools:metaValue><![CDATA[false]]></drools:metaValue>\n" +
                    "        </drools:metaData>\n" +
                    "      </bpmn2:extensionElements>\n" +
                    "      <bpmn2:conditionExpression xsi:type=\"bpmn2:tFormalExpression\" id=\"_k5STL3lcEeiHSaC1r8ADkg\" language=\"http://www.java.com/java\"><![CDATA[]]></bpmn2:conditionExpression>\n" +
                    "    </bpmn2:sequenceFlow>\n" +
                    "  </bpmn2:process>\n" +
                    "  <bpmndi:BPMNDiagram id=\"_k5STMHlcEeiHSaC1r8ADkg\">\n" +
                    "    <bpmndi:BPMNPlane id=\"_k5STMXlcEeiHSaC1r8ADkg\" bpmnElement=\"controlpoints.flaffo\">\n" +
                    "      <bpmndi:BPMNShape id=\"shape__64110518-0C20-4648-8C38-1EB22D08FFD2\" bpmnElement=\"_64110518-0C20-4648-8C38-1EB22D08FFD2\">\n" +
                    "        <dc:Bounds height=\"102.0\" width=\"154.0\" x=\"236.0\" y=\"77.0\"/>\n" +
                    "      </bpmndi:BPMNShape>\n" +
                    "      <bpmndi:BPMNShape id=\"shape__D1B6E315-4A1C-4057-8255-209AE84B9650\" bpmnElement=\"_D1B6E315-4A1C-4057-8255-209AE84B9650\">\n" +
                    "        <dc:Bounds height=\"56.0\" width=\"56.0\" x=\"854.0\" y=\"249.0\"/>\n" +
                    "      </bpmndi:BPMNShape>\n" +
                    "      <bpmndi:BPMNShape id=\"shape__259F0683-FF03-45C6-AB8D-798301E2F41D\" bpmnElement=\"_259F0683-FF03-45C6-AB8D-798301E2F41D\">\n" +
                    "        <dc:Bounds height=\"102.0\" width=\"154.0\" x=\"398.0\" y=\"331.0\"/>\n" +
                    "      </bpmndi:BPMNShape>\n" +
                    "      <bpmndi:BPMNShape id=\"shape__3201662B-28C8-48AF-AE4A-6AFF92C99A67\" bpmnElement=\"_3201662B-28C8-48AF-AE4A-6AFF92C99A67\">\n" +
                    "        <dc:Bounds height=\"56.0\" width=\"56.0\" x=\"100.0\" y=\"100.0\"/>\n" +
                    "      </bpmndi:BPMNShape>\n" +
                    "      <bpmndi:BPMNEdge id=\"edge_shape__3201662B-28C8-48AF-AE4A-6AFF92C99A67_to_shape__64110518-0C20-4648-8C38-1EB22D08FFD2\" bpmnElement=\"_D52F943C-450D-415A-AFC1-53D7887F7779\">\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"128.0\" y=\"128.0\"/>\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"313.0\" y=\"128.0\"/>\n" +
                    "      </bpmndi:BPMNEdge>\n" +
                    "      <bpmndi:BPMNEdge id=\"edge_shape__64110518-0C20-4648-8C38-1EB22D08FFD2_to_shape__259F0683-FF03-45C6-AB8D-798301E2F41D\" bpmnElement=\"_F1FAC58C-37B0-4ED7-B423-3CCD174261B9\">\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"313.0\" y=\"128.0\"/>\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"475.0\" y=\"331.0\"/>\n" +
                    "      </bpmndi:BPMNEdge>\n" +
                    "      <bpmndi:BPMNEdge id=\"edge_shape__259F0683-FF03-45C6-AB8D-798301E2F41D_to_shape__D1B6E315-4A1C-4057-8255-209AE84B9650\" bpmnElement=\"_957A6475-0F28-4CFB-BA40-3AD69692BD8D\">\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"475.0\" y=\"382.0\"/>\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"765.0\" y=\"405.0\"/>\n" +
                    "        <di:waypoint xsi:type=\"dc:Point\" x=\"882.0\" y=\"277.0\"/>\n" +
                    "      </bpmndi:BPMNEdge>\n" +
                    "    </bpmndi:BPMNPlane>\n" +
                    "  </bpmndi:BPMNDiagram>\n" +
                    "  <bpmn2:relationship id=\"_k5S6MHlcEeiHSaC1r8ADkg\" type=\"BPSimData\">\n" +
                    "    <bpmn2:extensionElements>\n" +
                    "      <bpsim:BPSimData>\n" +
                    "        <bpsim:Scenario xsi:type=\"bpsim:Scenario\" id=\"default\" name=\"Simulationscenario\">\n" +
                    "          <bpsim:ScenarioParameters xsi:type=\"bpsim:ScenarioParameters\"/>\n" +
                    "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_64110518-0C20-4648-8C38-1EB22D08FFD2\" id=\"_k5S6MXlcEeiHSaC1r8ADkg\">\n" +
                    "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
                    "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:NormalDistribution mean=\"0.0\" standardDeviation=\"0.0\"/>\n" +
                    "              </bpsim:ProcessingTime>\n" +
                    "            </bpsim:TimeParameters>\n" +
                    "            <bpsim:ResourceParameters xsi:type=\"bpsim:ResourceParameters\">\n" +
                    "              <bpsim:Availability xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                    "              </bpsim:Availability>\n" +
                    "              <bpsim:Quantity xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                    "              </bpsim:Quantity>\n" +
                    "            </bpsim:ResourceParameters>\n" +
                    "            <bpsim:CostParameters xsi:type=\"bpsim:CostParameters\">\n" +
                    "              <bpsim:UnitCost xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                    "              </bpsim:UnitCost>\n" +
                    "            </bpsim:CostParameters>\n" +
                    "          </bpsim:ElementParameters>\n" +
                    "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_259F0683-FF03-45C6-AB8D-798301E2F41D\" id=\"_k5S6MnlcEeiHSaC1r8ADkg\">\n" +
                    "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
                    "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:NormalDistribution mean=\"0.0\" standardDeviation=\"0.0\"/>\n" +
                    "              </bpsim:ProcessingTime>\n" +
                    "            </bpsim:TimeParameters>\n" +
                    "            <bpsim:ResourceParameters xsi:type=\"bpsim:ResourceParameters\">\n" +
                    "              <bpsim:Availability xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                    "              </bpsim:Availability>\n" +
                    "              <bpsim:Quantity xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                    "              </bpsim:Quantity>\n" +
                    "            </bpsim:ResourceParameters>\n" +
                    "            <bpsim:CostParameters xsi:type=\"bpsim:CostParameters\">\n" +
                    "              <bpsim:UnitCost xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:FloatingParameter value=\"0.0\"/>\n" +
                    "              </bpsim:UnitCost>\n" +
                    "            </bpsim:CostParameters>\n" +
                    "          </bpsim:ElementParameters>\n" +
                    "          <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_3201662B-28C8-48AF-AE4A-6AFF92C99A67\" id=\"_k5S6M3lcEeiHSaC1r8ADkg\">\n" +
                    "            <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">\n" +
                    "              <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">\n" +
                    "                <bpsim:NormalDistribution mean=\"0.0\" standardDeviation=\"0.0\"/>\n" +
                    "              </bpsim:ProcessingTime>\n" +
                    "            </bpsim:TimeParameters>\n" +
                    "          </bpsim:ElementParameters>\n" +
                    "        </bpsim:Scenario>\n" +
                    "      </bpsim:BPSimData>\n" +
                    "    </bpmn2:extensionElements>\n" +
                    "    <bpmn2:source>_k5OowXlcEeiHSaC1r8ADkg</bpmn2:source>\n" +
                    "    <bpmn2:target>_k5OowXlcEeiHSaC1r8ADkg</bpmn2:target>\n" +
                    "  </bpmn2:relationship>\n" +
                    "</bpmn2:definitions>";
}

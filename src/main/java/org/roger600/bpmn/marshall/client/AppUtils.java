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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;

import static java.util.stream.Collectors.toList;

public class AppUtils {

    public static Process getProcess(DocumentRoot docRoot) {
        return (Process) docRoot.getDefinitions().getRootElements().stream()
                .filter(p -> p instanceof Process)
                .findAny()
                .get();
    }

    public static String testString(DocumentRoot docRoot) {
        Process process = getProcess(docRoot);
        List<Class<?>> flowElementClasses = process.getFlowElements().stream().map(FlowElement::getClass).collect(toList());
        String result = (String.valueOf(flowElementClasses));
        return result;
    }

    public static Optional<MetaDataType> getMetaDataAttribute(final List<ExtensionAttributeValue> extensionValues,
                                                              final String metaDataName) {
        if (extensionValues != null) {
            Optional m = extensionValues.stream()
                    .map(ExtensionAttributeValue::getValue)
                    .flatMap((Function<FeatureMap, Stream<?>>) extensionElements -> {
                        List o = (List) extensionElements.get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true);
                        return o.stream();
                    })
                    .filter(metaType -> isMetaType((MetaDataType) metaType, metaDataName))
                    .findFirst();
            return m;
        }
        return Optional.empty();
    }

    public static String getMetaDataValue(final List<ExtensionAttributeValue> extensionValues,
                                          final String metaDataName) {

        Optional<MetaDataType> metaDataAttribute = getMetaDataAttribute(extensionValues, metaDataName);
        return metaDataAttribute.map(m -> m.getMetaValue()).orElse(null);
    }

    public static void setMetaDataValue(final List<ExtensionAttributeValue> extensionValues,
                                        final String metaDataName,
                                        final String value) {
        Optional<MetaDataType> metaDataAttribute = getMetaDataAttribute(extensionValues, metaDataName);
        metaDataAttribute.ifPresent(m -> m.setMetaValue(value));
    }

    public static boolean isMetaType(MetaDataType metaType,
                                     final String metaDataName) {
        return metaType.getName() != null &&
                metaType.getName().equals(metaDataName) &&
                metaType.getMetaValue() != null &&
                metaType.getMetaValue().length() > 0;
    }
}

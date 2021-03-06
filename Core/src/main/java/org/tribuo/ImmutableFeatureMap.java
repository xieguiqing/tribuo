/*
 * Copyright (c) 2015-2020, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tribuo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ImmutableFeatureMap is used when unknown features should not be added to the FeatureMap.
 * <p>
 * It's also got feature ids as those are only generated for immutable maps.
 * <p>
 * The feature ids are generated by sorting the feature names by the String comparator.
 * This ensures that any Example with sorted names has sorted int ids, even if some of
 * those features are unobserved. This is an extremely important property of {@link Feature}s,
 * {@link Example}s and {@link ImmutableFeatureMap}.
 */
public class ImmutableFeatureMap extends FeatureMap implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Map<Integer,VariableIDInfo> idMap;

    protected int size;

    public ImmutableFeatureMap(FeatureMap map) {
        this(generateIDs(map));
    }

    public ImmutableFeatureMap(List<VariableInfo> infoList) {
        this(generateIDs(infoList));
    }

    private ImmutableFeatureMap(Map<String,VariableIDInfo> map) {
        super(map);
        idMap = new HashMap<>();
        for (Map.Entry<String, VariableInfo> e : m.entrySet()) {
            VariableIDInfo idInfo = (VariableIDInfo) e.getValue();
            idMap.put(idInfo.getID(),idInfo);
        }
        size = m.size();
    }

    protected ImmutableFeatureMap() {
        super();
        idMap = new HashMap<>();
    }

    /**
     * Gets the {@link VariableIDInfo}
     * for this id number. Returns null if it's unknown.
     * @param id The id number to lookup.
     * @return The VariableInfo, or null.
     */
    public VariableIDInfo get(int id) {
        return idMap.get(id);
    }

    /**
     * Gets the {@link VariableIDInfo}
     * for this name. Returns null if it's unknown.
     * @param name The name to lookup.
     * @return The VariableInfo, or null.
     */
    @Override
    public VariableIDInfo get(String name) {
        return (VariableIDInfo) super.get(name);
    }

    /**
     * Gets the id number for this feature, returns -1 if it's unknown.
     * @param name The name of the feature.
     * @return A non-negative integer if the feature is known, -1 otherwise.
     */
    public int getID(String name) {
        VariableIDInfo info = get(name);
        if (info != null) {
            return info.getID();
        } else {
            return -1;
        }
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Generates the feature ids by sorting the features with the String comparator,
     * then sequentially numbering them.
     * @param map A feature map to convert.
     * @return A map from feature names to VariableIDInfo objects.
     */
    public static Map<String,VariableIDInfo> generateIDs(FeatureMap map) {
        TreeMap<String,VariableInfo> sortedMap = new TreeMap<>(map.m);
        return generateIDs(sortedMap);
    }

    /**
     * Generates the feature ids by sorting the features with the String comparator,
     * then sequentially numbering them.
     * @param list A list of {@link VariableInfo}s to generate a map from.
     * @return A map from feature names to VariableIDInfo objects.
     */
    public static Map<String,VariableIDInfo> generateIDs(List<? extends VariableInfo> list) {
        TreeMap<String,VariableInfo> sortedMap = new TreeMap<>();
        for (VariableInfo m : list) {
            sortedMap.put(m.getName(),m);
        }
        return generateIDs(sortedMap);
    }

    /**
     * Generates the feature ids from a sorted Map.
     * @param sortedMap A sorted map of the VariableInfos.
     * @return A map from feature names to VariableIDInfo objects.
     */
    private static Map<String, VariableIDInfo> generateIDs(TreeMap<String, VariableInfo> sortedMap) {
        Map<String,VariableIDInfo> outputMap = new HashMap<>();
        int counter = 0;
        for (Map.Entry<String, VariableInfo> e : sortedMap.entrySet()) {
            VariableIDInfo newInfo = e.getValue().makeIDInfo(counter);
            outputMap.put(e.getKey(),newInfo);
            counter++;
        }
        return outputMap;
    }

}

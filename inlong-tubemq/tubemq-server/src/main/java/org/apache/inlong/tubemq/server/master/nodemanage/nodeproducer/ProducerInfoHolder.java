/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.tubemq.server.master.nodemanage.nodeproducer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.inlong.tubemq.corebase.cluster.ProducerInfo;
import org.apache.inlong.tubemq.server.master.metrics.MasterMetricsHolder;

public class ProducerInfoHolder {

    final ConcurrentHashMap<String/* producerId */, ProducerInfo> producerInfoMap =
            new ConcurrentHashMap<>();

    public ProducerInfo getProducerInfo(String producerId) {
        return producerInfoMap.get(producerId);
    }

    public void setProducerInfo(String producerId,
                                        Set<String> topicSet,
                                        String host, boolean overTLS) {
        if (producerInfoMap.put(producerId,
                new ProducerInfo(producerId, topicSet, host, overTLS)) == null) {
            MasterMetricsHolder.incProducerCnt();
        }
    }

    public void updateProducerInfo(String producerId,
                                   Set<String> topicSet,
                                   String host,
                                   boolean overTLS) {
        ProducerInfo curProducer =
                producerInfoMap.get(producerId);
        ProducerInfo newProducer =
                new ProducerInfo(producerId, topicSet, host, overTLS);
        if (!newProducer.equals(curProducer)) {
            producerInfoMap.put(producerId, newProducer);
        }
    }

    public ProducerInfo removeProducer(String producerId, boolean isTimeout) {
        ProducerInfo info = producerInfoMap.remove(producerId);
        if (info != null) {
            MasterMetricsHolder.decProducerCnt(isTimeout);
        }
        return info;
    }

    public void clear() {
        producerInfoMap.clear();
    }
}

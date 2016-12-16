/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */

package com.mpush.common.handler;


import com.mpush.api.Message;
import com.mpush.api.MessageHandler;
import com.mpush.api.connection.Connection;
import com.mpush.api.protocol.Packet;
import com.mpush.common.message.BaseMessage;
import com.mpush.tools.Jsons;
import com.mpush.tools.common.Profiler;
import com.mpush.tools.common.Reflects;

import static com.mpush.api.protocol.Packet.FLAG_JSON_BODY;

/**
 * Created by ohun on 2015/12/22.
 *
 * @author ohun@live.cn
 */
public abstract class BaseMessageHandler<T extends BaseMessage> implements MessageHandler {
    @SuppressWarnings("unchecked")
    private Class<T> clazz = Reflects.getSuperClassGenericType(this.getClass(), 0);

    public abstract T decode(Packet packet, Connection connection);

    public abstract void handle(T message);

    public void handle(Packet packet, Connection connection) {
        Profiler.enter("time cost on [message decode]");
        T t = decode0(packet, connection);
        Profiler.release();

        if (t != null) {
            Profiler.enter("time cost on [handle]");
            handle(t);
            Profiler.release();
        }
    }

    protected T decode0(Packet packet, Connection connection) {
        if (packet.hasFlag(FLAG_JSON_BODY)) {
            String body = packet.getBody();
            T t = Jsons.fromJson(body, clazz);
            if (t != null) {
                t.setPacket(packet);
                t.setConnection(connection);
            }
            return Jsons.fromJson(body, clazz);
        } else {
            return decode(packet, connection);
        }
    }
}

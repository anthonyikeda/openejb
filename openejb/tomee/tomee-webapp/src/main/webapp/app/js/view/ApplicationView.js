/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

TOMEE.ApplicationView = function () {
    "use strict";

    var channel = TOMEE.ApplicationChannel,
        panelMap = {
            'console':TOMEE.ApplicationTabConsole(),
            'log':TOMEE.ApplicationTabLog()
        },
        selected = null,
        container = $(TOMEE.ApplicationTemplates.getValue('application', {})),
        connectionPopup = $(TOMEE.ApplicationTemplates.getValue('application-disconnected-popup', {})),
        toolbar = TOMEE.ApplicationToolbarView(),
        myWindow = $(window),
        delayedContainerResize = TOMEE.DelayedTask();

    channel.bind('server-connection', 'socket-connection-opened', function (data) {

    });

    channel.bind('server-connection', 'socket-connection-closed', function (data) {

    });

    channel.bind('server-connection', 'socket-connection-error', function (data) {

    });

    channel.bind('ui-actions', 'toolbar-click', function (data) {
        switchPanel(data.key);
    });

    //disable default contextmenu
    $(document).bind("contextmenu", function (e) {
        return false;
    });

    myWindow.on('resize', function () {
        delayedContainerResize.delay(updateContainerSize, 500);
    });

    myWindow.on('keyup', function (ev) {
        if (ev.keyCode === 18) { //ALT
            channel.send('ui-actions', 'window-alt-released', {});
        } else if (ev.keyCode === 17) { //CONTROL
            channel.send('ui-actions', 'window-ctrl-released', {});
        } else if (ev.keyCode === 16) { //SHIFT
            channel.send('ui-actions', 'window-shift-released', {});
        }
    });

    myWindow.on('keydown', function (ev) {
        var key = [],
            keyStr = null;

        if (ev.altKey) {
            key.push('alt');
        } else if (ev.ctrlKey) {
            key.push('ctrl');
        } else if (ev.shiftKey) {
            key.push('shift');
        }

        if (key.length === 0 &&
            !(ev.keyCode >= 112 && ev.keyCode <= 123 || ev.keyCode === 27)) { // F1...F12 or esc
            return; //nothing to do
        }

        keyStr = TOMEE.utils.keyCodeToString(ev.keyCode);
        if (!keyStr) {
            keyStr = ev.keyCode;
        }
        key.push(keyStr);

        channel.send('ui-actions', 'window-' + key.join('-') + '-pressed', {});
        ev.preventDefault();
    });

    function switchPanel(key) {
        if (selected) {
            selected.getEl().detach();
            selected.onDetach();
        }
        selected = panelMap[key];
        selected.getEl().appendTo(container);
        selected.onAppend();

        updateContainerSize();
    }

    function updateContainerSize() {
        var containerHeight,
            containerWidth,
            toolbarHeight = toolbar.getEl().height();

        containerHeight = myWindow.height();
        containerWidth = myWindow.width();

        container.css('height', containerHeight + 'px');
        container.css('width', containerWidth + 'px');

        channel.send('ui-actions', 'container-resized', {
            containerHeight:containerHeight - toolbarHeight,
            containerWidth:containerWidth - toolbarHeight
        });
    }

    return {
        render:function () {
            var myBody = $('body');
            container.append(toolbar.getEl());
            myBody.append(container);

            switchPanel('console');
            updateContainerSize();
        }
    };
};
(function() {
    function Pa() {
        if (-1 == G("RAIL_EXPIRATION")) for (var a = 0; 10 > a; a++) H(function() {
            (new ia).getFingerPrint()
        }, 20 + 2E3 * Math.pow(a, 2));
        else(new ia).getFingerPrint();
        H(function() {
            r.setInterval(function() {
                (new ia).getFingerPrint()
            }, 3E5)
        }, 3E5)
    }
    function mb(a) {
        this.isTimeout = 0;
        var b = this,
            c = r.RTCPeerConnection || r.webkitRTCPeerConnection || r.mozRTCPeerConnection;
        if ("function" == typeof c) {
            try {
                var d = new c({
                    iceServers: []
                });
                d.createDataChannel("", {
                    reliable: !1
                })
            } catch (f) {
                if (2 != b.isTimeout) {
                    b.isTimeout = 1;
                    a();
                    return
                }
            }
            var e = !1;
            d.onicecandidate = function(c) {
                var d = /([0-9]{1,3}(\.[0-9]{1,3}){3})/,
                    f = [];
                "complete" != c.target.iceGatheringState || e || (e = !0, c.target.localDescription.sdp.split("\n").forEach(function(a) {
                    (a = d.exec(a)) && "127.0.0.1" != a[1] && "0.0.0.0" != a[1] && -1 === f.indexOf(a[1]) && f.push(a[1])
                }), 2 != b.isTimeout && (b.isTimeout = 1, a({
                    localAddr: 0 < f.length ? f.sort()[0] : ""
                })))
            };
            d.onaddstream = function(a) {
                remoteVideo.src = r.URL.createObjectURL(a.stream)
            };
            d.createOffer(function(a) {
                d.setLocalDescription(a, function() {},

                    function() {})
            }, function() {}, {})
        } else a();
        H(function() {
            0 == b.isTimeout && (b.isTimeout = 2, a())
        }, 500)
    }
    function xa(a) {
        var b = a.length,
            c = 0 == b % 3 ? parseInt(b / 3) : parseInt(b / 3) + 1;
        if (3 > b) return a;
        var d = a.substring(0, 1 * c),
            e = a.substring(1 * c, 2 * c);
        return a.substring(2 * c, b) + d + e
    }
    function nb(a) {
        var b = a.split(".");
        if (4 !== b.length) throw Error("Invalid format -- expecting a.b.c.d");
        for (var c = a = 0; c < b.length; ++c) {
            var d = parseInt(b[c], 10);
            if (Number.isNaN(d) || 0 > d || 255 < d) throw Error("Each octet must be between 0 and 255");
            a |= d << 8 * (b.length - c - 1);
            a >>>= 0
        }
        return a
    }
    function ya(a) {
        if (!a) return "";
        if (ob(a)) return a.replace(/\s/g, ""); - 1 != a.indexOf("://") && (a = a.substr(a.indexOf("://") + 3));
        var b = "com net org gov edu mil biz name info mobi pro travel museum int areo post rec".split(" "),
            c = a.split(".");
        if (1 >= c.length || !isNaN(c[c.length - 1])) return a;
        for (a = 0; a < b.length && b[a] != c[c.length - 1];) a++;
        if (a != b.length) return "." + c[c.length - 2] + "." + c[c.length - 1];
        for (a = 0; a < b.length && b[a] != c[c.length - 2];) a++;
        return a == b.length ? c[c.length - 2] + "." + c[c.length - 1] : "." + c[c.length - 3] + "." + c[c.length - 2] + "." + c[c.length - 1]
    }
    function Qa(a) {
        return null != /[\\\"<>\.;]/.exec(a) && "undefined" != typeof encodeURIComponent ? encodeURIComponent(a) : a
    }
    function P(a, b) {
        if (Ra) {
            var c = b ? "visible" : "hidden";
            Q && K(a) ? K(a).style.visibility = c : Sa("#" + a, "visibility:" + c)
        }
    }
    function Sa(a, b, c, d) {
        if (!n.ie || !n.mac) {
            var e = q.getElementsByTagName("head")[0];
            e && (c = c && "string" == typeof c ? c : "screen", d && (za = L = null), L && za == c || (d = q.createElement("style"), d.setAttribute("type", "text/css"), d.setAttribute("media",
                c), L = e.appendChild(d), n.ie && n.win && "undefined" != typeof q.styleSheets && 0 < q.styleSheets.length && (L = q.styleSheets[q.styleSheets.length - 1]), za = c), n.ie && n.win ? L && "object" == typeof L.addRule && L.addRule(a, b) : L && "undefined" != typeof q.createTextNode && L.appendChild(q.createTextNode(a + " {" + b + "}")))
        }
    }
    function ja(a) {
        var b = n.pv;
        a = a.split(".");
        a[0] = parseInt(a[0], 10);
        a[1] = parseInt(a[1], 10) || 0;
        a[2] = parseInt(a[2], 10) || 0;
        return b[0] > a[0] || b[0] == a[0] && b[1] > a[1] || b[0] == a[0] && b[1] == a[1] && b[2] >= a[2] ? !0 : !1
    }
    function K(a) {
        var b = null;
        try {
            b = q.getElementById(a)
        } catch (c) {}
        return b
    }
    function Ta(a) {
        var b = K(a);
        b && "OBJECT" == b.nodeName && (n.ie && n.win ? (b.style.display = "none", function d() {
            if (4 == b.readyState) {
                var e = K(a);
                if (e) {
                    for (var f in e) "function" == typeof e[f] && (e[f] = null);
                    e.parentNode.removeChild(e)
                }
            } else H(d, 10)
        }()) : b.parentNode.removeChild(b))
    }
    function Aa(a, b, c) {
        var d, e = K(c);
        if (n.wk && 312 > n.wk) return d;
        if (e) if ("undefined" == typeof a.id && (a.id = c), n.ie && n.win) {
            var f = "",
                g;
            for (g in a) a[g] != Object.prototype[g] && ("data" == g.toLowerCase() ? b.movie = a[g] : "styleclass" == g.toLowerCase() ? f += ' class\x3d"' + a[g] + '"' : "classid" != g.toLowerCase() && (f += " " + g + '\x3d"' + a[g] + '"'));
            g = "";
            for (var l in b) b[l] != Object.prototype[l] && (g += '\x3cparam name\x3d"' + l + '" value\x3d"' + b[l] + '" /\x3e');
            e.outerHTML = '\x3cobject classid\x3d"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"' + f + "\x3e" + g + "\x3c/object\x3e";
            ka[ka.length] = a.id;
            d = K(a.id)
        } else {
            l = q.createElement("object");
            l.setAttribute("type", "application/x-shockwave-flash");
            for (var k in a) a[k] != Object.prototype[k] && ("styleclass" == k.toLowerCase() ? l.setAttribute("class", a[k]) : "classid" != k.toLowerCase() && l.setAttribute(k, a[k]));
            for (f in b) b[f] != Object.prototype[f] && "movie" != f.toLowerCase() && (a = l, g = f, k = b[f], c = q.createElement("param"), c.setAttribute("name", g), c.setAttribute("value", k), a.appendChild(c));
            e.parentNode.replaceChild(l, e);
            d = l
        }
        return d
    }
    function Ba(a) {
        var b = q.createElement("div");
        if (n.win && n.ie) b.innerHTML = a.innerHTML;
        else if (a = a.getElementsByTagName("object")[0]) if (a = a.childNodes) for (var c = a.length, d = 0; d < c; d++) 1 == a[d].nodeType && "PARAM" == a[d].nodeName || 8 == a[d].nodeType || b.appendChild(a[d].cloneNode(!0));
        return b
    }
    function pb(a) {
        if (n.ie && n.win && 4 != a.readyState) {
            var b = q.createElement("div");
            a.parentNode.insertBefore(b, a);
            b.parentNode.replaceChild(Ba(a), b);
            a.style.display = "none";
            (function d() {
                4 == a.readyState ? a.parentNode.removeChild(a) : H(d, 10)
            })()
        } else a.parentNode.replaceChild(Ba(a), a)
    }
    function Ca(a, b, c, d) {
        la = !0;
        Da = d || null;
        Ua = {
            success: !1,
            id: c
        };
        var e = K(c);
        if (e) {
            "OBJECT" == e.nodeName ? (Z = Ba(e), ma = null) : (Z = e, ma = c);
            a.id = "SWFObjectExprInst";
            if ("undefined" == typeof a.width || !/%$/.test(a.width) && 310 > parseInt(a.width, 10)) a.width = "310";
            if ("undefined" == typeof a.height || !/%$/.test(a.height) && 137 > parseInt(a.height, 10)) a.height = "137";
            q.title = q.title.slice(0, 47) + " - Flash Player Installation";
            d = n.ie && n.win ? "ActiveX" : "PlugIn";
            d = "MMredirectURL\x3d" + J.location.toString().replace(/&/g, "%26") + "\x26MMplayerType\x3d" + d + "\x26MMdoctitle\x3d" + q.title;
            b.flashvars = "undefined" != typeof b.flashvars ? b.flashvars + ("\x26" + d) : d;
            n.ie && n.win && 4 != e.readyState && (d = q.createElement("div"), c += "SWFObjectNew", d.setAttribute("id", c), e.parentNode.insertBefore(d, e), e.style.display = "none", function g() {
                4 == e.readyState ? e.parentNode.removeChild(e) : H(g, 10)
            }());
            Aa(a, b, c)
        }
    }
    function Ea() {
        return !la && ja("6.0.65") && (n.win || n.mac) && !(n.wk && 312 > n.wk)
    }
    function Fa(a) {
        var b = null;
        (a = K(a)) && "OBJECT" == a.nodeName && ("undefined" != typeof a.SetVariable ? b = a : (a = a.getElementsByTagName("object")[0]) && (b = a));
        return b
    }
    function Ga() {
        var a = M.length;
        if (0 < a) for (var b = 0; b < a; b++) {
            var c = M[b].id,
                d = M[b].callbackFn,
                e = {
                    success: !1,
                    id: c
                };
            if (0 < n.pv[0]) {
                var f = K(c);
                if (f) if (!ja(M[b].swfVersion) || n.wk && 312 > n.wk) if (M[b].expressInstall && Ea()) {
                    e = {};
                    e.data = M[b].expressInstall;
                    e.width = f.getAttribute("width") || "0";
                    e.height = f.getAttribute("height") || "0";
                    f.getAttribute("class") && (e.styleclass = f.getAttribute("class"));
                    f.getAttribute("align") && (e.align = f.getAttribute("align"));
                    for (var g = {}, f = f.getElementsByTagName("param"), l = f.length, k = 0; k < l; k++) "movie" != f[k].getAttribute("name").toLowerCase() && (g[f[k].getAttribute("name")] = f[k].getAttribute("value"));
                    Ca(e, g, c, d)
                } else pb(f), d && d(e);
                else P(c, !0), d && (e.success = !0, e.ref = Fa(c), d(e))
            } else P(c, !0), d && ((c = Fa(c)) && "undefined" != typeof c.SetVariable && (e.success = !0, e.ref = c), d(e))
        }
    }
    function Va(a) {
        if ("undefined" != typeof J.addEventListener) J.addEventListener("load", a, !1);
        else if ("undefined" != typeof q.addEventListener) q.addEventListener("load", a, !1);
        else if ("undefined" != typeof J.attachEvent) {
            var b = J;
            b.attachEvent("onload", a);
            U[U.length] = [b, "onload", a]
        } else if ("function" == typeof J.onload) {
            var c = J.onload;
            J.onload = function() {
                c();
                a()
            }
        } else J.onload = a
    }
    function Wa(a) {
        Q ? a() : na[na.length] = a
    }
    function V() {
        if (!Q) {
            try {
                var a = q.getElementsByTagName("body")[0].appendChild(q.createElement("span"));
                a.parentNode.removeChild(a)
            } catch (c) {
                return
            }
            Q = !0;
            for (var a = na.length, b = 0; b < a; b++) na[b]()
        }
    }
    function Xa(a) {
        return 4294967296 * (a - (a | 0)) | 0
    }
    function aa(a) {
        if (!(this instanceof aa)) return new aa(a);
        this.options = this.extend(a, {
            userDefinedFonts: [],
            swfContainerId: "fingerprintjs2",
            sortPluginsFor: [/palemoon/i],
            detectScreenOrientation: !0,
            swfPath: "flash/compiled/FontList.swf"
        });
        this.nativeForEach = Array.prototype.forEach;
        this.nativeMap = Array.prototype.map
    }
    function D(a, b, c, d, e, f, g) {
        return A(b & d | c & ~d, a, b, e, f, g)
    }
    function E(a, b, c, d, e, f, g) {
        return A(c ^ (b | ~d), a, b, e, f, g)
    }
    function ba(a) {
        for (var b = [], c = 0; c < 8 * a.length; c += 8) b[c >> 5] |= (a.charCodeAt(c / 8) & 255) << c % 32;
        a = 8 * a.length;
        b[a >> 5] |= 128 << a % 32;
        b[(a + 64 >>> 9 << 4) + 14] = a;
        a = 1732584193;
        for (var c = -271733879, d = -1732584194, e = 271733878, f = 0; f < b.length; f += 16) {
            var g = a,
                l = c,
                k = d,
                n = e;
            a = F(a, c, d, e, b[f + 0], 7, -680876936);
            e = F(e, a, c, d, b[f + 1], 12, -389564586);
            d = F(d, e, a, c, b[f + 2], 17, 606105819);
            c = F(c, d, e, a, b[f + 3], 22, -1044525330);
            a = F(a, c, d, e, b[f + 4], 7, -176418897);
            e = F(e, a, c, d, b[f + 5], 12, 1200080426);
            d = F(d, e, a, c, b[f + 6], 17, -1473231341);
            c = F(c, d, e, a, b[f + 7], 22, -45705983);
            a = F(a, c, d, e, b[f + 8], 7, 1770035416);
            e = F(e, a, c, d, b[f + 9], 12, -1958414417);
            d = F(d, e, a, c, b[f + 10], 17, -42063);
            c = F(c, d, e, a, b[f + 11], 22, -1990404162);
            a = F(a, c, d, e, b[f + 12], 7, 1804603682);
            e = F(e, a, c, d, b[f + 13], 12, -40341101);
            d = F(d, e, a, c, b[f + 14], 17, -1502002290);
            c = F(c, d, e, a, b[f + 15], 22, 1236535329);
            a = D(a, c, d, e, b[f + 1], 5, -165796510);
            e = D(e, a, c, d, b[f + 6], 9, -1069501632);
            d = D(d, e, a, c, b[f + 11], 14, 643717713);
            c = D(c, d, e, a, b[f + 0], 20, -373897302);
            a = D(a, c, d, e, b[f + 5], 5, -701558691);
            e = D(e, a, c, d, b[f + 10], 9, 38016083);
            d = D(d, e, a, c, b[f + 15], 14, -660478335);
            c = D(c, d, e, a, b[f + 4], 20, -405537848);
            a = D(a, c, d, e, b[f + 9], 5, 568446438);
            e = D(e, a, c, d, b[f + 14], 9, -1019803690);
            d = D(d, e, a, c, b[f + 3], 14, -187363961);
            c = D(c, d, e, a, b[f + 8], 20, 1163531501);
            a = D(a, c, d, e, b[f + 13], 5, -1444681467);
            e = D(e, a, c, d, b[f + 2], 9, -51403784);
            d = D(d, e, a, c, b[f + 7], 14, 1735328473);
            c = D(c, d, e, a, b[f + 12], 20, -1926607734);
            a = A(c ^ d ^ e, a, c, b[f + 5], 4, -378558);
            e = A(a ^ c ^ d, e, a, b[f + 8], 11, -2022574463);
            d = A(e ^ a ^ c, d, e, b[f + 11], 16, 1839030562);
            c = A(d ^ e ^ a, c, d, b[f + 14], 23, -35309556);
            a = A(c ^ d ^ e, a, c, b[f + 1], 4, -1530992060);
            e = A(a ^ c ^ d, e, a, b[f + 4], 11, 1272893353);
            d = A(e ^ a ^ c, d, e, b[f + 7], 16, -155497632);
            c = A(d ^ e ^ a, c, d, b[f + 10], 23, -1094730640);
            a = A(c ^ d ^ e, a, c, b[f + 13], 4, 681279174);
            e = A(a ^ c ^ d, e, a, b[f + 0], 11, -358537222);
            d = A(e ^ a ^ c, d, e, b[f + 3], 16, -722521979);
            c = A(d ^ e ^ a, c, d, b[f + 6], 23, 76029189);
            a = A(c ^ d ^ e, a, c, b[f + 9], 4, -640364487);
            e = A(a ^ c ^ d, e, a, b[f + 12], 11, -421815835);
            d = A(e ^ a ^ c, d, e, b[f + 15], 16, 530742520);
            c = A(d ^ e ^ a, c, d, b[f + 2], 23, -995338651);
            a = E(a, c, d, e, b[f + 0], 6, -198630844);
            e = E(e, a, c, d, b[f + 7], 10, 1126891415);
            d = E(d, e, a, c, b[f + 14], 15, -1416354905);
            c = E(c, d, e, a, b[f + 5], 21, -57434055);
            a = E(a, c, d, e, b[f + 12], 6, 1700485571);
            e = E(e, a, c, d, b[f + 3], 10, -1894986606);
            d = E(d, e, a, c, b[f + 10], 15, -1051523);
            c = E(c, d, e, a, b[f + 1], 21, -2054922799);
            a = E(a, c, d, e, b[f + 8], 6, 1873313359);
            e = E(e, a, c, d, b[f + 15], 10, -30611744);
            d = E(d, e, a, c, b[f + 6],
                15, -1560198380);
            c = E(c, d, e, a, b[f + 13], 21, 1309151649);
            a = E(a, c, d, e, b[f + 4], 6, -145523070);
            e = E(e, a, c, d, b[f + 11], 10, -1120210379);
            d = E(d, e, a, c, b[f + 2], 15, 718787259);
            c = E(c, d, e, a, b[f + 9], 21, -343485551);
            a = N(a, g);
            c = N(c, l);
            d = N(d, k);
            e = N(e, n)
        }
        b = [a, c, d, e];
        a = qb ? "0123456789ABCDEF" : "0123456789abcdef";
        c = "";
        for (d = 0; d < 4 * b.length; d++) c += a.charAt(b[d >> 2] >> d % 4 * 8 + 4 & 15) + a.charAt(b[d >> 2] >> d % 4 * 8 & 15);
        return c
    }
    function N(a, b) {
        var c = (a & 65535) + (b & 65535);
        return (a >> 16) + (b >> 16) + (c >> 16) << 16 | c & 65535
    }
    function A(a, b, c, d, e, f) {
        a = N(N(b, a), N(d,
            f));
        return N(a << e | a >>> 32 - e, c)
    }
    function F(a, b, c, d, e, f, g) {
        return A(b & c | ~b & d, a, b, e, f, g)
    }
    function G(a) {
        var b, c, d, e = u.cookie.split(";");
        for (b = 0; b < e.length; b++) if (c = e[b].substr(0, e[b].indexOf("\x3d")), d = e[b].substr(e[b].indexOf("\x3d") + 1), c = c.replace(/^\s+|\s+$/g, ""), a = a.replace(/^\s+|\s+$/g, ""), c == a) return unescape(d)
    }
    function W(a, b, c, d, e, f) {
        var g = new Date;
        g.setTime(g.getTime()); - 1 != c ? (c *= 864E5, g = new Date(g.getTime() + c), cookieString = a + "\x3d" + escape(b) + (c ? ";expires\x3d" + g.toGMTString() : "") + (d ? ";path\x3d" + d : "") + (e ? ";domain\x3d" + e : "") + (f ? ";secure" : "")) : (g = -1, cookieString = a + "\x3d" + escape(b) + (c ? ";expires\x3d" + g : "") + (d ? ";path\x3d" + d : "") + (e ? ";domain\x3d" + e : "") + (f ? ";secure" : ""));
        u.cookie = cookieString
    }
    function ob(a) {
        a = a.replace(/\s/g, "");
        if (/^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/.test(a)) {
            a = a.split(".");
            if (0 == parseInt(parseFloat(a[0])) || 0 == parseInt(parseFloat(a[3]))) return !1;
            for (var b = 0; b < a.length; b++) if (255 < parseInt(parseFloat(a[b]))) return !1;
            return !0
        }
        return !1
    }
    function p(a, b) {
        this.key = a;
        this.value = b
    }
    function W(a,
               b, c) {
        var d = new Date;
        d.setTime(d.getTime() + 864E5 * Number(c));
        u.cookie = a + "\x3d" + b + "; path\x3d/;expires \x3d " + d.toGMTString() + ";domain\x3d" + ya(r.location.host.split(":")[0])
    }
    function Ha() {
        var a = k.userAgent.toLowerCase();
        return 0 <= a.indexOf("windows phone") ? "WindowsPhone" : 0 <= a.indexOf("win") ? "Windows" : 0 <= a.indexOf("android") ? "Android" : 0 <= a.indexOf("linux") ? "Linux" : 0 <= a.indexOf("iphone") || 0 <= a.indexOf("ipad") ? "iOS" : 0 <= a.indexOf("mac") ? "Mac" : "Other"
    }
    function ia() {
        this.ec = new evercookie;
        this.deviceEc = new evercookie;
        this.cfp = new aa;
        this.packageString = "";
        this.moreInfoArray = []
    }
    var u = document,
        r = window,
        k = navigator,
        y = screen,
        H = setTimeout,
        rb = top,
        sb = location,
        tb = parent,
        Ya = ["WEB", "WAP"];
    Array.prototype.indexOf || (Array.prototype.indexOf = function(a, b) {
        var c;
        if (null == this) throw new TypeError("'this' is null or undefined");
        var d = Object(this),
            e = d.length >>> 0;
        if (0 === e) return -1;
        c = +b || 0;
        Infinity === Math.abs(c) && (c = 0);
        if (c >= e) return -1;
        for (c = Math.max(0 <= c ? c : e - Math.abs(c), 0); c < e;) {
            if (c in d && d[c] === a) return c;
            c++
        }
        return -1
    });
    "object" != typeof JSON && (JSON = {});
    (function() {
        function a(a) {
            return 10 > a ? "0" + a : a
        }
        function b() {
            return this.valueOf()
        }
        function c(a) {
            return m.lastIndex = 0, m.test(a) ? '"' + a.replace(m, function(a) {
                var b = g[a];
                return "string" == typeof b ? b : "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4)
            }) + '"' : '"' + a + '"'
        }
        function d(a, b) {
            var v, g, z, B, h, k = e,
                m = b[a];
            switch (m && "object" == typeof m && "function" == typeof m.toJSON && (m = m.toJSON(a)), "function" == typeof l && (m = l.call(b, a, m)), typeof m) {
                case "string":
                    return c(m);
                case "number":
                    return isFinite(m) ? String(m) : "null";
                case "boolean":
                case "null":
                    return String(m);
                case "object":
                    if (!m) return "null";
                    if (e += f, h = [], "[object Array]" === Object.prototype.toString.apply(m)) {
                        B = m.length;
                        for (v = 0; B > v; v += 1) h[v] = d(v, m) || "null";
                        return z = 0 === h.length ? "[]" : e ? "[\n" + e + h.join(",\n" + e) + "\n" + k + "]" : "[" + h.join(",") + "]", e = k, z
                    }
                    if (l && "object" == typeof l) for (B = l.length, v = 0; B > v; v += 1) "string" == typeof l[v] && (g = l[v], z = d(g, m), z && h.push(c(g) + (e ? ": " : ":") + z));
                    else for (g in m) Object.prototype.hasOwnProperty.call(m, g) && (z = d(g, m), z && h.push(c(g) + (e ? ": " : ":") + z));
                    return z = 0 === h.length ? "{}" : e ? "{\n" + e + h.join(",\n" + e) + "\n" + k + "}" : "{" + h.join(",") + "}", e = k, z
            }
        }
        var e, f, g, l, k = /^[\],:{}\s]*$/,
            n = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,
            t = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
            h = /(?:^|:|,)(?:\s*\[)+/g,
            m = /[\\\"\u0000-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
            p = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
        "function" != typeof Date.prototype.toJSON && (Date.prototype.toJSON = function() {
            return isFinite(this.valueOf()) ? this.getUTCFullYear() + "-" + a(this.getUTCMonth() + 1) + "-" + a(this.getUTCDate()) + "T" + a(this.getUTCHours()) + ":" + a(this.getUTCMinutes()) + ":" + a(this.getUTCSeconds()) + "Z" : null
        }, Boolean.prototype.toJSON = b, Number.prototype.toJSON = b, String.prototype.toJSON = b);
        "function" != typeof JSON.stringify && (g = {
            "\\": "\\\\",
            "   ": "\\t",
            '"': '\\"',
            "\b": "\\b",
            "\r": "\\r",
            "\n": "\\n",
            "\f": "\\f"
        }, JSON.stringify = function(a, b,
                                     c) {
            var v;
            if (e = "", f = "", "number" == typeof c) for (v = 0; c > v; v += 1) f += " ";
            else "string" == typeof c && (f = c);
            if (l = b, b && "function" != typeof b && ("object" != typeof b || "number" != typeof b.length)) throw Error("JSON.stringify");
            return d("", {
                "": a
            })
        });
        "function" != typeof JSON.parse && (JSON.parse = function(a, b) {
            function c(a, d) {
                var e, f, v = a[d];
                if (v && "object" == typeof v) for (e in v) Object.prototype.hasOwnProperty.call(v, e) && (f = c(v, e), void 0 !== f ? v[e] = f : delete v[e]);
                return b.call(a, d, v)
            }
            var d;
            if (a = String(a), p.lastIndex = 0, p.test(a) && (a = a.replace(p, function(a) {
                return "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4)
            })), k.test(a.replace(n, "@").replace(t, "]").replace(h, ""))) return d = eval("(" + a + ")"), "function" == typeof b ? c({
                "": d
            }, "") : d;
            throw new SyntaxError("JSON.parse");
        })
    })();
    debug = !1;
    var Za = {
        getJSON: function(a, b, c) {
            b = u.createElement("script");
            b.type = "text/javascript";
            b.src = a;
            b.id = "id_callbackFunction";
            r.callbackFunction = function(a) {
                r.callbackFunction = void 0;
                var b = u.getElementById("id_callbackFunction");
                b && Za.removeElem(b);
                c(a)
            };
            (a = u.getElementsByTagName("head")) && a[0] && a[0].appendChild(b)
        },
        now: function() {
            return (new Date).getTime()
        },
        removeElem: function(a) {
            var b = a.parentNode;
            if (b) try {
                b.removeChild(a)
            } catch (c) {}
        },
        parseData: function(a) {
            var b = "";
            if ("string" === typeof a) b = a;
            else if ("object" === typeof a) for (var c in a) b += "\x26" + c + "\x3d" + encodeURIComponent(a[c]);
            b += "\x26_time\x3d" + this.now();
            return b = b.substr(1)
        },
        rand: function() {
            return Math.random().toString().substr(2)
        }
    }, Ia;
    if (!(Ia = R)) {
        var ca = Math,
            oa = {}, pa = oa.lib = {}, $a = function() {},
            da = pa.Base = {
                create: function() {
                    var a = this.extend();
                    a.init.apply(a, arguments);
                    return a
                },
                extend: function(a) {
                    $a.prototype = this;
                    var b = new $a;
                    a && b.mixIn(a);
                    b.hasOwnProperty("init") || (b.init = function() {
                        b.$super.init.apply(this, arguments)
                    });
                    b.init.prototype = b;
                    b.$super = this;
                    return b
                },
                clone: function() {
                    return this.init.prototype.extend(this)
                },
                init: function() {},
                mixIn: function(a) {
                    for (var b in a) a.hasOwnProperty(b) && (this[b] = a[b]);
                    a.hasOwnProperty("toString") && (this.toString = a.toString)
                }
            }, ea = pa.WordArray = da.extend({
                clamp: function() {
                    var a = this.words,
                        b = this.sigBytes;
                    a[b >>> 2] &= 4294967295 << 32 - b % 4 * 8;
                    a.length = ca.ceil(b / 4)
                },
                random: function(a) {
                    for (var b = [], c = 0; c < a; c += 4) b.push(4294967296 * ca.random() | 0);
                    return new ea.init(b, a)
                },
                init: function(a, b) {
                    a = this.words = a || [];
                    this.sigBytes = void 0 != b ? b : 4 * a.length
                },
                concat: function(a) {
                    var b = this.words,
                        c = a.words,
                        d = this.sigBytes;
                    a = a.sigBytes;
                    this.clamp();
                    if (d % 4) for (var e = 0; e < a; e++) b[d + e >>> 2] |= (c[e >>> 2] >>> 24 - e % 4 * 8 & 255) << 24 - (d + e) % 4 * 8;
                    else if (65535 < c.length) for (e = 0; e < a; e += 4) b[d + e >>> 2] = c[e >>> 2];
                    else b.push.apply(b,
                            c);
                    this.sigBytes += a;
                    return this
                },
                clone: function() {
                    var a = da.clone.call(this);
                    a.words = this.words.slice(0);
                    return a
                },
                toString: function(a) {
                    return (a || ub).stringify(this)
                }
            }),
            Ja = oa.enc = {}, ub = Ja.Hex = {
                parse: function(a) {
                    for (var b = a.length, c = [], d = 0; d < b; d += 2) c[d >>> 3] |= parseInt(a.substr(d, 2), 16) << 24 - d % 8 * 4;
                    return new ea.init(c, b / 2)
                },
                stringify: function(a) {
                    var b = a.words;
                    a = a.sigBytes;
                    for (var c = [], d = 0; d < a; d++) {
                        var e = b[d >>> 2] >>> 24 - d % 4 * 8 & 255;
                        c.push((e >>> 4).toString(16));
                        c.push((e & 15).toString(16))
                    }
                    return c.join("")
                }
            },
            ab = Ja.Latin1 = {
                parse: function(a) {
                    for (var b = a.length, c = [], d = 0; d < b; d++) c[d >>> 2] |= (a.charCodeAt(d) & 255) << 24 - d % 4 * 8;
                    return new ea.init(c, b)
                },
                stringify: function(a) {
                    var b = a.words;
                    a = a.sigBytes;
                    for (var c = [], d = 0; d < a; d++) c.push(String.fromCharCode(b[d >>> 2] >>> 24 - d % 4 * 8 & 255));
                    return c.join("")
                }
            }, vb = Ja.Utf8 = {
                stringify: function(a) {
                    try {
                        return decodeURIComponent(escape(ab.stringify(a)))
                    } catch (b) {
                        throw Error("Malformed UTF-8 data");
                    }
                },
                parse: function(a) {
                    return ab.parse(unescape(encodeURIComponent(a)))
                }
            }, bb = pa.BufferedBlockAlgorithm = da.extend({
                reset: function() {
                    this._data = new ea.init;
                    this._nDataBytes = 0
                },
                _append: function(a) {
                    "string" == typeof a && (a = vb.parse(a));
                    this._data.concat(a);
                    this._nDataBytes += a.sigBytes
                },
                _process: function(a) {
                    var b = this._data,
                        c = b.words,
                        d = b.sigBytes,
                        e = this.blockSize,
                        f = d / (4 * e),
                        f = a ? ca.ceil(f) : ca.max((f | 0) - this._minBufferSize, 0);
                    a = f * e;
                    d = ca.min(4 * a, d);
                    if (a) {
                        for (var g = 0; g < a; g += e) this._doProcessBlock(c, g);
                        g = c.splice(0, a);
                        b.sigBytes -= d
                    }
                    return new ea.init(g, d)
                },
                _minBufferSize: 0,
                clone: function() {
                    var a = da.clone.call(this);
                    a._data = this._data.clone();
                    return a
                }
            });
        pa.Hasher = bb.extend({
            reset: function() {
                bb.reset.call(this);
                this._doReset()
            },
            cfg: da.extend(),
            init: function(a) {
                this.cfg = this.cfg.extend(a);
                this.reset()
            },
            _createHmacHelper: function(a) {
                return function(b, c) {
                    return (new wb.HMAC.init(a, c)).finalize(b)
                }
            },
            update: function(a) {
                this._append(a);
                this._process();
                return this
            },
            blockSize: 16,
            finalize: function(a) {
                a && this._append(a);
                return this._doFinalize()
            },
            _createHelper: function(a) {
                return function(b, c) {
                    return (new a.init(c)).finalize(b)
                }
            }
        });
        var wb = oa.algo = {};
        Ia = oa
    }
    for (var R = Ia, qa = Math, ra = R, S = ra.lib, xb = S.WordArray, sa = S.Hasher, S = ra.algo, cb = [], db = [], ta = 2, fa = 0; 64 > fa;) {
        var X;
        a: {
            X = ta;
            for (var yb = qa.sqrt(X), Ka = 2; Ka <= yb; Ka++) if (!(X % Ka)) {
                X = !1;
                break a
            }
            X = !0
        }
        X && (8 > fa && (cb[fa] = Xa(qa.pow(ta, .5))), db[fa] = Xa(qa.pow(ta, 1 / 3)), fa++);
        ta++
    }
    var T = [],
        S = S.SHA256 = sa.extend({
            _doFinalize: function() {
                var a = this._data,
                    b = a.words,
                    c = 8 * this._nDataBytes,
                    d = 8 * a.sigBytes;
                b[d >>> 5] |= 128 << 24 - d % 32;
                b[(d + 64 >>> 9 << 4) + 14] = qa.floor(c / 4294967296);
                b[(d + 64 >>> 9 << 4) + 15] = c;
                a.sigBytes = 4 * b.length;
                this._process();
                return this._hash
            },
            _doProcessBlock: function(a, b) {
                for (var c = this._hash.words, d = c[0], e = c[1], f = c[2], g = c[3], l = c[4], k = c[5], n = c[6], t = c[7], h = 0; 64 > h; h++) {
                    if (16 > h) T[h] = a[b + h] | 0;
                    else {
                        var m = T[h - 15],
                            p = T[h - 2];
                        T[h] = ((m << 25 | m >>> 7) ^ (m << 14 | m >>> 18) ^ m >>> 3) + T[h - 7] + ((p << 15 | p >>> 17) ^ (p << 13 | p >>> 19) ^ p >>> 10) + T[h - 16]
                    }
                    m = t + ((l << 26 | l >>> 6) ^ (l << 21 | l >>> 11) ^ (l << 7 | l >>> 25)) + (l & k ^ ~l & n) + db[h] + T[h];
                    p = ((d << 30 | d >>> 2) ^ (d << 19 | d >>> 13) ^ (d << 10 | d >>> 22)) + (d & e ^ d & f ^ e & f);
                    t = n;
                    n = k;
                    k = l;
                    l = g + m | 0;
                    g = f;
                    f = e;
                    e = d;
                    d = m + p | 0
                }
                c[0] = c[0] + d | 0;
                c[1] = c[1] + e | 0;
                c[2] = c[2] + f | 0;
                c[3] = c[3] + g | 0;
                c[4] = c[4] + l | 0;
                c[5] = c[5] + k | 0;
                c[6] = c[6] + n | 0;
                c[7] = c[7] + t | 0
            },
            clone: function() {
                var a = sa.clone.call(this);
                a._hash = this._hash.clone();
                return a
            },
            _doReset: function() {
                this._hash = new xb.init(cb.slice(0))
            }
        });
    ra.SHA256 = sa._createHelper(S);
    ra.HmacSHA256 = sa._createHmacHelper(S);
    var eb = R,
        zb = eb.lib.WordArray;
    eb.enc.Base64 = {
        parse: function(a) {
            var b = a.length,
                c = this._map,
                d = c.charAt(64);
            d && (d = a.indexOf(d), -1 != d && (b = d));
            for (var d = [], e = 0, f = 0; f < b; f++) if (f % 4) {
                var g = c.indexOf(a.charAt(f - 1)) << f % 4 * 2,
                    l = c.indexOf(a.charAt(f)) >>> 6 - f % 4 * 2;
                d[e >>> 2] |= (g | l) << 24 - e % 4 * 8;
                e++
            }
            return zb.create(d, e)
        },
        _map: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_",
        stringify: function(a) {
            var b = a.words,
                c = a.sigBytes,
                d = this._map;
            a.clamp();
            a = [];
            for (var e = 0; e < c; e += 3) for (var f = (b[e >>> 2] >>> 24 - e % 4 * 8 & 255) << 16 | (b[e + 1 >>> 2] >>> 24 - (e + 1) % 4 * 8 & 255) << 8 | b[e + 2 >>> 2] >>> 24 - (e + 2) % 4 * 8 & 255, g = 0; 4 > g && e + .75 * g < c; g++) a.push(d.charAt(f >>> 6 * (3 - g) & 63));
            if (b = d.charAt(64)) for (; a.length % 4;) a.push(b);
            return a.join("")
        }
    };
    aa.prototype = {
        getRegularPlugins: function() {
            for (var a = [], b = 0, c = k.plugins.length; b < c; b++) a.push(k.plugins[b]);
            this.pluginsShouldBeSorted() && (a = a.sort(function(a, b) {
                return a.name > b.name ? 1 : a.name < b.name ? -1 : 0
            }));
            return this.map(a, function(a) {
                var b = this.map(a, function(a) {
                    return [a.type, a.suffixes].join("~")
                }).join(",");
                return [a.name, a.description, b].join("::")
            }, this)
        },
        sessionStorageKey: function(a) {
            !this.options.excludeSessionStorage && this.hasSessionStorage() && a.push({
                key: "session_storage",
                value: 1
            });
            return a
        },
        x64Add: function(a,
                         b) {
            a = [a[0] >>> 16, a[0] & 65535, a[1] >>> 16, a[1] & 65535];
            b = [b[0] >>> 16, b[0] & 65535, b[1] >>> 16, b[1] & 65535];
            var c = [0, 0, 0, 0];
            c[3] += a[3] + b[3];
            c[2] += c[3] >>> 16;
            c[3] &= 65535;
            c[2] += a[2] + b[2];
            c[1] += c[2] >>> 16;
            c[2] &= 65535;
            c[1] += a[1] + b[1];
            c[0] += c[1] >>> 16;
            c[1] &= 65535;
            c[0] += a[0] + b[0];
            c[0] &= 65535;
            return [c[0] << 16 | c[1], c[2] << 16 | c[3]]
        },
        adBlockKey: function(a) {
            this.options.excludeAdBlock || a.push({
                key: "adblock",
                value: this.getAdBlock()
            });
            return a
        },
        hasLiedOsKey: function(a) {
            this.options.excludeHasLiedOs || a.push({
                value: this.getHasLiedOs(),
                key: "has_lied_os"
            });
            return a
        },
        cpuClassKey: function(a) {
            this.options.excludeCpuClass || a.push({
                key: "cpu_class",
                value: this.getNavigatorCpuClass()
            });
            return a
        },
        getIEPlugins: function() {
            var a = [];
            if (Object.getOwnPropertyDescriptor && Object.getOwnPropertyDescriptor(r, "ActiveXObject") || "ActiveXObject" in r) a = this.map("AcroPDF.PDF;Adodb.Stream;AgControl.AgControl;DevalVRXCtrl.DevalVRXCtrl.1;MacromediaFlashPaper.MacromediaFlashPaper;Msxml2.DOMDocument;Msxml2.XMLHTTP;PDF.PdfCtrl;QuickTime.QuickTime;QuickTimeCheckObject.QuickTimeCheck.1;RealPlayer;RealPlayer.RealPlayer(tm) ActiveX Control (32-bit);RealVideo.RealVideo(tm) ActiveX Control (32-bit);Scripting.Dictionary;SWCtl.SWCtl;Shell.UIHelper;ShockwaveFlash.ShockwaveFlash;Skype.Detection;TDCCtl.TDCCtl;WMPlayer.OCX;rmocx.RealPlayer G2 Control;rmocx.RealPlayer G2 Control.1".split(";"),

                function(a) {
                    try {
                        return new ActiveXObject(a), a
                    } catch (c) {
                        return null
                    }
                });
            k.plugins && (a = a.concat(this.getRegularPlugins()));
            return a
        },
        x64Fmix: function(a) {
            a = this.x64Xor(a, [0, a[0] >>> 1]);
            a = this.x64Multiply(a, [4283543511, 3981806797]);
            a = this.x64Xor(a, [0, a[0] >>> 1]);
            a = this.x64Multiply(a, [3301882366, 444984403]);
            return a = this.x64Xor(a, [0, a[0] >>> 1])
        },
        hasSwfObjectLoaded: function() {
            return "undefined" !== typeof r.swfobject
        },
        getWebglCanvas: function() {
            var a = u.createElement("canvas"),
                b = null;
            try {
                b = a.getContext("webgl") || a.getContext("experimental-webgl")
            } catch (c) {}
            b || (b = null);
            return b
        },
        hasLiedResolutionKey: function(a) {
            this.options.excludeHasLiedResolution || a.push({
                key: "has_lied_resolution",
                value: this.getHasLiedResolution()
            });
            return a
        },
        hasMinFlashInstalled: function() {
            return Y.hasFlashPlayerVersion("9.0.0")
        },
        getAvailableScreenResolution: function(a) {
            var b;
            y.availWidth && y.availHeight && (b = this.options.detectScreenOrientation ? y.availHeight > y.availWidth ? [y.availHeight, y.availWidth] : [y.availWidth, y.availHeight] : [y.availHeight,
                y.availWidth]);
            "undefined" !== typeof b && a.push({
                value: b,
                key: "available_resolution"
            });
            return a
        },
        getNavigatorCpuClass: function() {
            return k.cpuClass ? k.cpuClass : "unknown"
        },
        pluginsKey: function(a) {
            this.options.excludePlugins || (this.isIE() ? this.options.excludeIEPlugins || a.push({
                key: "ie_plugins",
                value: this.getIEPlugins()
            }) : a.push({
                value: this.getRegularPlugins(),
                key: "regular_plugins"
            }));
            return a
        },
        getCanvasFp: function() {
            var a = [],
                b = u.createElement("canvas");
            b.width = 2E3;
            b.height = 200;
            b.style.display = "inline";
            var c = b.getContext("2d");
            c.rect(0, 0, 10, 10);
            c.rect(2, 2, 6, 6);
            a.push("canvas winding:" + (!1 === c.isPointInPath(5, 5, "evenodd") ? "yes" : "no"));
            c.textBaseline = "alphabetic";
            c.fillStyle = "#f60";
            c.fillRect(125, 1, 62, 20);
            c.fillStyle = "#069";
            c.font = this.options.dontUseFakeFontInCanvas ? "11pt Arial" : "11pt no-real-font-123";
            c.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03", 2, 15);
            c.fillStyle = "rgba(102, 204, 0, 0.2)";
            c.font = "18pt Arial";
            c.fillText("Cwm fjordbank glyphs vext quiz, \ud83d\ude03", 4, 45);
            c.globalCompositeOperation =
                "multiply";
            c.fillStyle = "rgb(255,0,255)";
            c.beginPath();
            c.arc(50, 50, 50, 0, 2 * Math.PI, !0);
            c.closePath();
            c.fill();
            c.fillStyle = "rgb(0,255,255)";
            c.beginPath();
            c.arc(100, 50, 50, 0, 2 * Math.PI, !0);
            c.closePath();
            c.fill();
            c.fillStyle = "rgb(255,255,0)";
            c.beginPath();
            c.arc(75, 100, 50, 0, 2 * Math.PI, !0);
            c.closePath();
            c.fill();
            c.fillStyle = "rgb(255,0,255)";
            c.arc(75, 75, 75, 0, 2 * Math.PI, !0);
            c.arc(75, 75, 25, 0, 2 * Math.PI, !0);
            c.fill("evenodd");
            a.push("canvas fp:" + b.toDataURL());
            return a.join("~")
        },
        x64hash128: function(a, b) {
            a = a || "";
            b = b || 0;
            for (var c = a.length % 16, d = a.length - c, e = [0, b], f = [0, b], g, l, k = [2277735313, 289559509], n = [1291169091, 658871167], t = 0; t < d; t += 16) g = [a.charCodeAt(t + 4) & 255 | (a.charCodeAt(t + 5) & 255) << 8 | (a.charCodeAt(t + 6) & 255) << 16 | (a.charCodeAt(t + 7) & 255) << 24, a.charCodeAt(t) & 255 | (a.charCodeAt(t + 1) & 255) << 8 | (a.charCodeAt(t + 2) & 255) << 16 | (a.charCodeAt(t + 3) & 255) << 24], l = [a.charCodeAt(t + 12) & 255 | (a.charCodeAt(t + 13) & 255) << 8 | (a.charCodeAt(t + 14) & 255) << 16 | (a.charCodeAt(t + 15) & 255) << 24, a.charCodeAt(t + 8) & 255 | (a.charCodeAt(t + 9) & 255) << 8 | (a.charCodeAt(t + 10) & 255) << 16 | (a.charCodeAt(t + 11) & 255) << 24], g = this.x64Multiply(g, k), g = this.x64Rotl(g, 31), g = this.x64Multiply(g, n), e = this.x64Xor(e, g), e = this.x64Rotl(e, 27), e = this.x64Add(e, f), e = this.x64Add(this.x64Multiply(e, [0, 5]), [0, 1390208809]), l = this.x64Multiply(l, n), l = this.x64Rotl(l, 33), l = this.x64Multiply(l, k), f = this.x64Xor(f, l), f = this.x64Rotl(f, 31), f = this.x64Add(f, e), f = this.x64Add(this.x64Multiply(f, [0, 5]), [0, 944331445]);
            g = [0, 0];
            l = [0, 0];
            switch (c) {
                case 15:
                    l = this.x64Xor(l, this.x64LeftShift([0, a.charCodeAt(t + 14)],
                        48));
                case 14:
                    l = this.x64Xor(l, this.x64LeftShift([0, a.charCodeAt(t + 13)], 40));
                case 13:
                    l = this.x64Xor(l, this.x64LeftShift([0, a.charCodeAt(t + 12)], 32));
                case 12:
                    l = this.x64Xor(l, this.x64LeftShift([0, a.charCodeAt(t + 11)], 24));
                case 11:
                    l = this.x64Xor(l, this.x64LeftShift([0, a.charCodeAt(t + 10)], 16));
                case 10:
                    l = this.x64Xor(l, this.x64LeftShift([0, a.charCodeAt(t + 9)], 8));
                case 9:
                    l = this.x64Xor(l, [0, a.charCodeAt(t + 8)]), l = this.x64Multiply(l, n), l = this.x64Rotl(l, 33), l = this.x64Multiply(l, k), f = this.x64Xor(f, l);
                case 8:
                    g = this.x64Xor(g,
                        this.x64LeftShift([0, a.charCodeAt(t + 7)], 56));
                case 7:
                    g = this.x64Xor(g, this.x64LeftShift([0, a.charCodeAt(t + 6)], 48));
                case 6:
                    g = this.x64Xor(g, this.x64LeftShift([0, a.charCodeAt(t + 5)], 40));
                case 5:
                    g = this.x64Xor(g, this.x64LeftShift([0, a.charCodeAt(t + 4)], 32));
                case 4:
                    g = this.x64Xor(g, this.x64LeftShift([0, a.charCodeAt(t + 3)], 24));
                case 3:
                    g = this.x64Xor(g, this.x64LeftShift([0, a.charCodeAt(t + 2)], 16));
                case 2:
                    g = this.x64Xor(g, this.x64LeftShift([0, a.charCodeAt(t + 1)], 8));
                case 1:
                    g = this.x64Xor(g, [0, a.charCodeAt(t)]), g = this.x64Multiply(g,
                        k), g = this.x64Rotl(g, 31), g = this.x64Multiply(g, n), e = this.x64Xor(e, g)
            }
            e = this.x64Xor(e, [0, a.length]);
            f = this.x64Xor(f, [0, a.length]);
            e = this.x64Add(e, f);
            f = this.x64Add(f, e);
            e = this.x64Fmix(e);
            f = this.x64Fmix(f);
            e = this.x64Add(e, f);
            f = this.x64Add(f, e);
            return ("00000000" + (e[0] >>> 0).toString(16)).slice(-8) + ("00000000" + (e[1] >>> 0).toString(16)).slice(-8) + ("00000000" + (f[0] >>> 0).toString(16)).slice(-8) + ("00000000" + (f[1] >>> 0).toString(16)).slice(-8)
        },
        getHasLiedOs: function() {
            var a = k.userAgent.toLowerCase(),
                b = k.oscpu,
                c = k.platform.toLowerCase(),
                a = 0 <= a.indexOf("windows phone") ? "Windows Phone" : 0 <= a.indexOf("win") ? "Windows" : 0 <= a.indexOf("android") ? "Android" : 0 <= a.indexOf("linux") ? "Linux" : 0 <= a.indexOf("iphone") || 0 <= a.indexOf("ipad") ? "iOS" : 0 <= a.indexOf("mac") ? "Mac" : "Other";
            return ("ontouchstart" in r || 0 < k.maxTouchPoints || 0 < k.msMaxTouchPoints) && "Windows Phone" !== a && "Android" !== a && "iOS" !== a && "Other" !== a || "undefined" !== typeof b && (b = b.toLowerCase(), 0 <= b.indexOf("win") && "Windows" !== a && "Windows Phone" !== a || 0 <= b.indexOf("linux") &&
            "Linux" !== a && "Android" !== a || 0 <= b.indexOf("mac") && "Mac" !== a && "iOS" !== a || 0 === b.indexOf("win") && 0 === b.indexOf("linux") && 0 <= b.indexOf("mac") && "other" !== a) ? !0 : 0 <= c.indexOf("win") && "Windows" !== a && "Windows Phone" !== a || (0 <= c.indexOf("linux") || 0 <= c.indexOf("android") || 0 <= c.indexOf("pike")) && "Linux" !== a && "Android" !== a || (0 <= c.indexOf("mac") || 0 <= c.indexOf("ipad") || 0 <= c.indexOf("ipod") || 0 <= c.indexOf("iphone")) && "Mac" !== a && "iOS" !== a || 0 === c.indexOf("win") && 0 === c.indexOf("linux") && 0 <= c.indexOf("mac") && "other" !== a ? !0 : "undefined" === typeof k.plugins && "Windows" !== a && "Windows Phone" !== a ? !0 : !1
        },
        getDoNotTrack: function() {
            return k.doNotTrack ? k.doNotTrack : k.msDoNotTrack ? k.msDoNotTrack : r.doNotTrack ? r.doNotTrack : "unknown"
        },
        hasLiedBrowserKey: function(a) {
            this.options.excludeHasLiedBrowser || a.push({
                key: "has_lied_browser",
                value: this.getHasLiedBrowser()
            });
            return a
        },
        x64Multiply: function(a, b) {
            a = [a[0] >>> 16, a[0] & 65535, a[1] >>> 16, a[1] & 65535];
            b = [b[0] >>> 16, b[0] & 65535, b[1] >>> 16, b[1] & 65535];
            var c = [0, 0, 0, 0];
            c[3] += a[3] * b[3];
            c[2] += c[3] >>> 16;
            c[3] &= 65535;
            c[2] += a[2] * b[3];
            c[1] += c[2] >>> 16;
            c[2] &= 65535;
            c[2] += a[3] * b[2];
            c[1] += c[2] >>> 16;
            c[2] &= 65535;
            c[1] += a[1] * b[3];
            c[0] += c[1] >>> 16;
            c[1] &= 65535;
            c[1] += a[2] * b[2];
            c[0] += c[1] >>> 16;
            c[1] &= 65535;
            c[1] += a[3] * b[1];
            c[0] += c[1] >>> 16;
            c[1] &= 65535;
            c[0] += a[0] * b[3] + a[1] * b[2] + a[2] * b[1] + a[3] * b[0];
            c[0] &= 65535;
            return [c[0] << 16 | c[1], c[2] << 16 | c[3]]
        },
        hasIndexedDB: function() {
            return !!r.indexedDB
        },
        userAgentKey: function(a) {
            this.options.excludeUserAgent || a.push({
                key: "user_agent",
                value: this.getUserAgent()
            });
            return a
        },
        extend: function(a,
                         b) {
            if (null == a) return b;
            for (var c in a) null != a[c] && b[c] !== a[c] && (b[c] = a[c]);
            return b
        },
        doNotTrackKey: function(a) {
            this.options.excludeDoNotTrack || a.push({
                value: this.getDoNotTrack(),
                key: "do_not_track"
            });
            return a
        },
        platformKey: function(a) {
            this.options.excludePlatform || a.push({
                key: "navigator_platform",
                value: this.getNavigatorPlatform()
            });
            return a
        },
        screenResolutionKey: function(a) {
            return this.options.excludeScreenResolution ? a : this.getScreenResolution(a)
        },
        getUserAgent: function() {
            var a = k.userAgent;
            return a = a.replace(/\&|\+|\?|\%|\#|\/|\=/g,
                "")
        },
        get: function(a) {
            var b = [],
                b = this.userAgentKey(b),
                b = this.languageKey(b),
                b = this.colorDepthKey(b),
                b = this.pixelRatioKey(b),
                b = this.screenResolutionKey(b),
                b = this.availableScreenResolutionKey(b),
                b = this.timezoneOffsetKey(b),
                b = this.sessionStorageKey(b),
                b = this.localStorageKey(b),
                b = this.indexedDbKey(b),
                b = this.addBehaviorKey(b),
                b = this.openDatabaseKey(b),
                b = this.cpuClassKey(b),
                b = this.platformKey(b),
                b = this.doNotTrackKey(b),
                b = this.pluginsKey(b),
                b = this.canvasKey(b),
                b = this.webglKey(b),
                b = this.adBlockKey(b),
                b = this.hasLiedLanguagesKey(b),
                b = this.hasLiedResolutionKey(b),
                b = this.hasLiedOsKey(b),
                b = this.hasLiedBrowserKey(b),
                b = this.touchSupportKey(b),
                c = this;
            this.fontsKey(b, function(b) {
                var d = [];
                c.each(b, function(a) {
                    var b = a.value;
                    "undefined" !== typeof a.value.join && (b = a.value.join(";"));
                    d.push(b)
                });
                var f = c.x64hash128(d.join("~~~"), 31);
                return a(f, b)
            })
        },
        languageKey: function(a) {
            this.options.excludeLanguage || a.push({
                value: k.language || k.userLanguage || k.browserLanguage || k.systemLanguage || "",
                key: "language"
            });
            return a
        },
        hasSessionStorage: function() {
            try {
                return !!r.sessionStorage
            } catch (a) {
                return !0
            }
        },
        x64Rotl: function(a, b) {
            b %= 64;
            if (32 === b) return [a[1], a[0]];
            if (32 > b) return [a[0] << b | a[1] >>> 32 - b, a[1] << b | a[0] >>> 32 - b];
            b -= 32;
            return [a[1] << b | a[0] >>> 32 - b, a[0] << b | a[1] >>> 32 - b]
        },
        getHasLiedBrowser: function() {
            var a = k.userAgent.toLowerCase(),
                b = k.productSub,
                a = 0 <= a.indexOf("firefox") ? "Firefox" : 0 <= a.indexOf("opera") || 0 <= a.indexOf("opr") ? "Opera" : 0 <= a.indexOf("chrome") ? "Chrome" : 0 <= a.indexOf("safari") ? "Safari" : 0 <= a.indexOf("trident") ? "Internet Explorer" : "Other";
            if (("Chrome" === a || "Safari" === a || "Opera" === a) && "20030107" !== b) return !0;
            b = eval.toString().length;
            if (37 === b && "Safari" !== a && "Firefox" !== a && "Other" !== a || 39 === b && "Internet Explorer" !== a && "Other" !== a || 33 === b && "Chrome" !== a && "Opera" !== a && "Other" !== a) return !0;
            var c;
            try {
                throw "a";
            } catch (d) {
                try {
                    d.toSource(), c = !0
                } catch (e) {
                    c = !1
                }
            }
            return c && "Firefox" !== a && "Other" !== a ? !0 : !1
        },
        x64Xor: function(a, b) {
            return [a[0] ^ b[0], a[1] ^ b[1]]
        },
        getTouchSupport: function() {
            var a = 0,
                b = !1;
            "undefined" !== typeof k.maxTouchPoints ? a = k.maxTouchPoints : "undefined" !== typeof k.msMaxTouchPoints && (a = k.msMaxTouchPoints);
            try {
                u.createEvent("TouchEvent"), b = !0
            } catch (c) {}
            return [a, b, "ontouchstart" in r]
        },
        flashFontsKey: function(a, b) {
            if (this.options.excludeFlashFonts || !this.hasSwfObjectLoaded() || !this.hasMinFlashInstalled() || "undefined" === typeof this.options.swfPath) return b(a);
            this.loadSwfAndDetectFonts(function(c) {
                a.push({
                    value: c.join(";"),
                    key: "swf_fonts"
                });
                b(a)
            })
        },
        each: function(a, b, c) {
            if (null !== a) if (this.nativeForEach && a.forEach === this.nativeForEach) a.forEach(b, c);
            else if (a.length === +a.length) for (var d = 0, e = a.length; d < e && b.call(c, a[d], d, a) !== {}; d++);
            else for (d in a) if (a.hasOwnProperty(d) && b.call(c, a[d], d, a) === {}) break
        },
        getHasLiedLanguages: function() {
            if ("undefined" !== typeof k.languages) try {
                if (k.languages[0].substr(0, 2) !== k.language.substr(0, 2)) return !0
            } catch (a) {
                return !0
            }
            return !1
        },
        availableScreenResolutionKey: function(a) {
            return this.options.excludeAvailableScreenResolution ? a : this.getAvailableScreenResolution(a)
        },
        x64LeftShift: function(a, b) {
            b %= 64;
            return 0 === b ? a : 32 > b ? [a[0] << b | a[1] >>> 32 - b, a[1] << b] : [a[1] << b - 32, 0]
        },
        getPixelRatio: function() {
            return r.devicePixelRatio ||
                ""
        },
        fontsKey: function(a, b) {
            return this.options.excludeJsFonts ? this.flashFontsKey(a, b) : this.jsFontsKey(a, b)
        },
        getScreenResolution: function(a) {
            var b;
            b = this.options.detectScreenOrientation ? y.height > y.width ? [y.height, y.width] : [y.width, y.height] : [y.width, y.height];
            "undefined" !== typeof b && a.push({
                value: b,
                key: "resolution"
            });
            return a
        },
        isIE: function() {
            return "Microsoft Internet Explorer" === k.appName || "Netscape" === k.appName && /Trident/.test(k.userAgent) ? !0 : !1
        },
        loadSwfAndDetectFonts: function(a) {
            r.___fp_swf_loaded = function(b) {
                a(b)
            };
            var b = this.options.swfContainerId;
            this.addFlashDivNode();
            Y.embedSWF(this.options.swfPath, b, "1", "1", "9.0.0", !1, {
                onReady: "___fp_swf_loaded"
            }, {
                menu: "false",
                allowScriptAccess: "always"
            }, {})
        },
        isCanvasSupported: function() {
            var a = u.createElement("canvas");
            return !(!a.getContext || !a.getContext("2d"))
        },
        hasLiedLanguagesKey: function(a) {
            this.options.excludeHasLiedLanguages || a.push({
                key: "has_lied_languages",
                value: this.getHasLiedLanguages()
            });
            return a
        },
        webglKey: function(a) {
            if (this.options.excludeWebGL || !this.isWebGlSupported()) return a;
            a.push({
                key: "webgl",
                value: this.getWebglFp()
            });
            return a
        },
        getAdBlock: function() {
            var a = u.createElement("div");
            a.innerHTML = "\x26nbsp;";
            a.className = "adsbox";
            var b = "0";
            try {
                u.body.appendChild(a), 0 === u.getElementsByClassName("adsbox")[0].offsetHeight && (b = "1"), u.body.removeChild(a)
            } catch (c) {
                b = "0"
            }
            return b
        },
        addFlashDivNode: function() {
            var a = u.createElement("div");
            a.setAttribute("id", this.options.swfContainerId);
            u.body.appendChild(a)
        },
        localStorageKey: function(a) {
            !this.options.excludeSessionStorage && this.hasLocalStorage() && a.push({
                value: 1,
                key: "local_storage"
            });
            return a
        },
        canvasKey: function(a) {
            !this.options.excludeCanvas && this.isCanvasSupported() && a.push({
                key: "canvas",
                value: this.getCanvasFp()
            });
            return a
        },
        map: function(a, b, c) {
            var d = [];
            if (null == a) return d;
            if (this.nativeMap && a.map === this.nativeMap) return a.map(b, c);
            this.each(a, function(a, f, g) {
                d[d.length] = b.call(c, a, f, g)
            });
            return d
        },
        colorDepthKey: function(a) {
            this.options.excludeColorDepth || a.push({
                value: y.colorDepth || -1,
                key: "color_depth"
            });
            return a
        },
        addBehaviorKey: function(a) {
            u.body && !this.options.excludeAddBehavior && u.body.addBehavior && a.push({
                value: 1,
                key: "add_behavior"
            });
            return a
        },
        getWebglFp: function() {
            function a(a) {
                b.clearColor(0, 0, 0, 1);
                b.enable(b.DEPTH_TEST);
                b.depthFunc(b.LEQUAL);
                b.clear(b.COLOR_BUFFER_BIT | b.DEPTH_BUFFER_BIT);
                return "[" + a[0] + ", " + a[1] + "]"
            }
            var b;
            b = this.getWebglCanvas();
            if (!b) return null;
            var c = [],
                d = b.createBuffer();
            b.bindBuffer(b.ARRAY_BUFFER, d);
            var e = new Float32Array([-.2, -.9, 0, .4, -.26, 0, 0, .732134444, 0]);
            b.bufferData(b.ARRAY_BUFFER,
                e, b.STATIC_DRAW);
            d.itemSize = 3;
            d.numItems = 3;
            var e = b.createProgram(),
                f = b.createShader(b.VERTEX_SHADER);
            b.shaderSource(f, "attribute vec2 attrVertex;varying vec2 varyinTexCoordinate;uniform vec2 uniformOffset;void main(){varyinTexCoordinate\x3dattrVertex+uniformOffset;gl_Position\x3dvec4(attrVertex,0,1);}");
            b.compileShader(f);
            var g = b.createShader(b.FRAGMENT_SHADER);
            b.shaderSource(g, "precision mediump float;varying vec2 varyinTexCoordinate;void main() {gl_FragColor\x3dvec4(varyinTexCoordinate,0,1);}");
            b.compileShader(g);
            b.attachShader(e, f);
            b.attachShader(e, g);
            b.linkProgram(e);
            b.useProgram(e);
            e.vertexPosAttrib = b.getAttribLocation(e, "attrVertex");
            e.offsetUniform = b.getUniformLocation(e, "uniformOffset");
            b.enableVertexAttribArray(e.vertexPosArray);
            b.vertexAttribPointer(e.vertexPosAttrib, d.itemSize, b.FLOAT, !1, 0, 0);
            b.uniform2f(e.offsetUniform, 1, 1);
            b.drawArrays(b.TRIANGLE_STRIP, 0, d.numItems);
            null != b.canvas && c.push(b.canvas.toDataURL());
            c.push("extensions:" + b.getSupportedExtensions().join(";"));
            c.push("webgl aliased line width range:" + a(b.getParameter(b.ALIASED_LINE_WIDTH_RANGE)));
            c.push("webgl aliased point size range:" + a(b.getParameter(b.ALIASED_POINT_SIZE_RANGE)));
            c.push("webgl alpha bits:" + b.getParameter(b.ALPHA_BITS));
            c.push("webgl antialiasing:" + (b.getContextAttributes().antialias ? "yes" : "no"));
            c.push("webgl blue bits:" + b.getParameter(b.BLUE_BITS));
            c.push("webgl depth bits:" + b.getParameter(b.DEPTH_BITS));
            c.push("webgl green bits:" + b.getParameter(b.GREEN_BITS));
            c.push("webgl max anisotropy:" + function(a) {
                var b, c = a.getExtension("EXT_texture_filter_anisotropic") || a.getExtension("WEBKIT_EXT_texture_filter_anisotropic") || a.getExtension("MOZ_EXT_texture_filter_anisotropic");
                return c ? (b = a.getParameter(c.MAX_TEXTURE_MAX_ANISOTROPY_EXT), 0 === b && (b = 2), b) : null
            }(b));
            c.push("webgl max combined texture image units:" + b.getParameter(b.MAX_COMBINED_TEXTURE_IMAGE_UNITS));
            c.push("webgl max cube map texture size:" + b.getParameter(b.MAX_CUBE_MAP_TEXTURE_SIZE));
            c.push("webgl max fragment uniform vectors:" + b.getParameter(b.MAX_FRAGMENT_UNIFORM_VECTORS));
            c.push("webgl max render buffer size:" + b.getParameter(b.MAX_RENDERBUFFER_SIZE));
            c.push("webgl max texture image units:" + b.getParameter(b.MAX_TEXTURE_IMAGE_UNITS));
            c.push("webgl max texture size:" + b.getParameter(b.MAX_TEXTURE_SIZE));
            c.push("webgl max varying vectors:" + b.getParameter(b.MAX_VARYING_VECTORS));
            c.push("webgl max vertex attribs:" + b.getParameter(b.MAX_VERTEX_ATTRIBS));
            c.push("webgl max vertex texture image units:" + b.getParameter(b.MAX_VERTEX_TEXTURE_IMAGE_UNITS));
            c.push("webgl max vertex uniform vectors:" + b.getParameter(b.MAX_VERTEX_UNIFORM_VECTORS));
            c.push("webgl max viewport dims:" + a(b.getParameter(b.MAX_VIEWPORT_DIMS)));
            c.push("webgl red bits:" + b.getParameter(b.RED_BITS));
            c.push("webgl renderer:" + b.getParameter(b.RENDERER));
            c.push("webgl shading language version:" + b.getParameter(b.SHADING_LANGUAGE_VERSION));
            c.push("webgl stencil bits:" + b.getParameter(b.STENCIL_BITS));
            c.push("webgl vendor:" + b.getParameter(b.VENDOR));
            c.push("webgl version:" + b.getParameter(b.VERSION));
            if (!b.getShaderPrecisionFormat) return c.join("~");
            c.push("webgl vertex shader high float precision:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.HIGH_FLOAT).precision);
            c.push("webgl vertex shader high float precision rangeMin:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.HIGH_FLOAT).rangeMin);
            c.push("webgl vertex shader high float precision rangeMax:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.HIGH_FLOAT).rangeMax);
            c.push("webgl vertex shader medium float precision:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.MEDIUM_FLOAT).precision);
            c.push("webgl vertex shader medium float precision rangeMin:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.MEDIUM_FLOAT).rangeMin);
            c.push("webgl vertex shader medium float precision rangeMax:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.MEDIUM_FLOAT).rangeMax);
            c.push("webgl vertex shader low float precision:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.LOW_FLOAT).precision);
            c.push("webgl vertex shader low float precision rangeMin:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.LOW_FLOAT).rangeMin);
            c.push("webgl vertex shader low float precision rangeMax:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.LOW_FLOAT).rangeMax);
            c.push("webgl fragment shader high float precision:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.HIGH_FLOAT).precision);
            c.push("webgl fragment shader high float precision rangeMin:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.HIGH_FLOAT).rangeMin);
            c.push("webgl fragment shader high float precision rangeMax:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.HIGH_FLOAT).rangeMax);
            c.push("webgl fragment shader medium float precision:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.MEDIUM_FLOAT).precision);
            c.push("webgl fragment shader medium float precision rangeMin:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.MEDIUM_FLOAT).rangeMin);
            c.push("webgl fragment shader medium float precision rangeMax:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.MEDIUM_FLOAT).rangeMax);
            c.push("webgl fragment shader low float precision:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.LOW_FLOAT).precision);
            c.push("webgl fragment shader low float precision rangeMin:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.LOW_FLOAT).rangeMin);
            c.push("webgl fragment shader low float precision rangeMax:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.LOW_FLOAT).rangeMax);
            c.push("webgl vertex shader high int precision:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.HIGH_INT).precision);
            c.push("webgl vertex shader high int precision rangeMin:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.HIGH_INT).rangeMin);
            c.push("webgl vertex shader high int precision rangeMax:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER,
                b.HIGH_INT).rangeMax);
            c.push("webgl vertex shader medium int precision:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.MEDIUM_INT).precision);
            c.push("webgl vertex shader medium int precision rangeMin:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.MEDIUM_INT).rangeMin);
            c.push("webgl vertex shader medium int precision rangeMax:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.MEDIUM_INT).rangeMax);
            c.push("webgl vertex shader low int precision:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.LOW_INT).precision);
            c.push("webgl vertex shader low int precision rangeMin:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.LOW_INT).rangeMin);
            c.push("webgl vertex shader low int precision rangeMax:" + b.getShaderPrecisionFormat(b.VERTEX_SHADER, b.LOW_INT).rangeMax);
            c.push("webgl fragment shader high int precision:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.HIGH_INT).precision);
            c.push("webgl fragment shader high int precision rangeMin:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.HIGH_INT).rangeMin);
            c.push("webgl fragment shader high int precision rangeMax:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.HIGH_INT).rangeMax);
            c.push("webgl fragment shader medium int precision:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.MEDIUM_INT).precision);
            c.push("webgl fragment shader medium int precision rangeMin:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.MEDIUM_INT).rangeMin);
            c.push("webgl fragment shader medium int precision rangeMax:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.MEDIUM_INT).rangeMax);
            c.push("webgl fragment shader low int precision:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.LOW_INT).precision);
            c.push("webgl fragment shader low int precision rangeMin:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.LOW_INT).rangeMin);
            c.push("webgl fragment shader low int precision rangeMax:" + b.getShaderPrecisionFormat(b.FRAGMENT_SHADER, b.LOW_INT).rangeMax);
            return c.join("~")
        },
        openDatabaseKey: function(a) {
            !this.options.excludeOpenDatabase && r.openDatabase && a.push({
                key: "open_database",
                value: 1
            });
            return a
        },
        pluginsShouldBeSorted: function() {
            for (var a = !1, b = 0, c = this.options.sortPluginsFor.length; b < c; b++) if (k.userAgent.match(this.options.sortPluginsFor[b])) {
                a = !0;
                break
            }
            return a
        },
        timezoneOffsetKey: function(a) {
            this.options.excludeTimezoneOffset || a.push({
                value: (new Date).getTimezoneOffset(),
                key: "timezone_offset"
            });
            return a
        },
        getNavigatorPlatform: function() {
            return k.platform ? k.platform : "unknown"
        },
        touchSupportKey: function(a) {
            this.options.excludeTouchSupport || a.push({
                key: "touch_support",
                value: this.getTouchSupport()
            });
            return a
        },
        hasLocalStorage: function() {
            try {
                return !!r.localStorage
            } catch (a) {
                return !0
            }
        },
        indexedDbKey: function(a) {
            !this.options.excludeIndexedDB && this.hasIndexedDB() && a.push({
                value: 1,
                key: "indexed_db"
            });
            return a
        },
        pixelRatioKey: function(a) {
            this.options.excludePixelRatio || a.push({
                value: this.getPixelRatio(),
                key: "pixel_ratio"
            });
            return a
        },
        jsFontsKey: function(a, b) {
            var c = this;
            return H(function() {
                function d() {
                    var a = u.createElement("span");
                    a.style.position = "absolute";
                    a.style.left = "-9999px";
                    a.style.fontSize = "72px";
                    a.style.lineHeight = "normal";
                    a.innerHTML = "mmmmmmmmmmlli";
                    return a
                }
                var e = ["monospace",
                        "sans-serif", "serif"],
                    f = "Andale Mono;Arial;Arial Black;Arial Hebrew;Arial MT;Arial Narrow;Arial Rounded MT Bold;Arial Unicode MS;Bitstream Vera Sans Mono;Book Antiqua;Bookman Old Style;Calibri;Cambria;Cambria Math;Century;Century Gothic;Century Schoolbook;Comic Sans;Comic Sans MS;Consolas;Courier;Courier New;Garamond;Geneva;Georgia;Helvetica;Helvetica Neue;Impact;Lucida Bright;Lucida Calligraphy;Lucida Console;Lucida Fax;LUCIDA GRANDE;Lucida Handwriting;Lucida Sans;Lucida Sans Typewriter;Lucida Sans Unicode;Microsoft Sans Serif;Monaco;Monotype Corsiva;MS Gothic;MS Outlook;MS PGothic;MS Reference Sans Serif;MS Sans Serif;MS Serif;MYRIAD;MYRIAD PRO;Palatino;Palatino Linotype;Segoe Print;Segoe Script;Segoe UI;Segoe UI Light;Segoe UI Semibold;Segoe UI Symbol;Tahoma;Times;Times New Roman;Trebuchet MS;Verdana;Wingdings;Wingdings 2;Wingdings 3".split(";"),
                    g = "Abadi MT Condensed Light;Academy Engraved LET;ADOBE CASLON PRO;Adobe Garamond;ADOBE GARAMOND PRO;Agency FB;Aharoni;Albertus Extra Bold;Albertus Medium;Algerian;Amazone BT;American Typewriter;American Typewriter Condensed;AmerType Md BT;Andalus;Angsana New;AngsanaUPC;Antique Olive;Aparajita;Apple Chancery;Apple Color Emoji;Apple SD Gothic Neo;Arabic Typesetting;ARCHER;ARNO PRO;Arrus BT;Aurora Cn BT;AvantGarde Bk BT;AvantGarde Md BT;AVENIR;Ayuthaya;Bandy;Bangla Sangam MN;Bank Gothic;BankGothic Md BT;Baskerville;Baskerville Old Face;Batang;BatangChe;Bauer Bodoni;Bauhaus 93;Bazooka;Bell MT;Bembo;Benguiat Bk BT;Berlin Sans FB;Berlin Sans FB Demi;Bernard MT Condensed;BernhardFashion BT;BernhardMod BT;Big Caslon;BinnerD;Blackadder ITC;BlairMdITC TT;Bodoni 72;Bodoni 72 Oldstyle;Bodoni 72 Smallcaps;Bodoni MT;Bodoni MT Black;Bodoni MT Condensed;Bodoni MT Poster Compressed;Bookshelf Symbol 7;Boulder;Bradley Hand;Bradley Hand ITC;Bremen Bd BT;Britannic Bold;Broadway;Browallia New;BrowalliaUPC;Brush Script MT;Californian FB;Calisto MT;Calligrapher;Candara;CaslonOpnface BT;Castellar;Centaur;Cezanne;CG Omega;CG Times;Chalkboard;Chalkboard SE;Chalkduster;Charlesworth;Charter Bd BT;Charter BT;Chaucer;ChelthmITC Bk BT;Chiller;Clarendon;Clarendon Condensed;CloisterBlack BT;Cochin;Colonna MT;Constantia;Cooper Black;Copperplate;Copperplate Gothic;Copperplate Gothic Bold;Copperplate Gothic Light;CopperplGoth Bd BT;Corbel;Cordia New;CordiaUPC;Cornerstone;Coronet;Cuckoo;Curlz MT;DaunPenh;Dauphin;David;DB LCD Temp;DELICIOUS;Denmark;DFKai-SB;Didot;DilleniaUPC;DIN;DokChampa;Dotum;DotumChe;Ebrima;Edwardian Script ITC;Elephant;English 111 Vivace BT;Engravers MT;EngraversGothic BT;Eras Bold ITC;Eras Demi ITC;Eras Light ITC;Eras Medium ITC;EucrosiaUPC;Euphemia;Euphemia UCAS;EUROSTILE;Exotc350 Bd BT;FangSong;Felix Titling;Fixedsys;FONTIN;Footlight MT Light;Forte;FrankRuehl;Fransiscan;Freefrm721 Blk BT;FreesiaUPC;Freestyle Script;French Script MT;FrnkGothITC Bk BT;Fruitger;FRUTIGER;Futura;Futura Bk BT;Futura Lt BT;Futura Md BT;Futura ZBlk BT;FuturaBlack BT;Gabriola;Galliard BT;Gautami;Geeza Pro;Geometr231 BT;Geometr231 Hv BT;Geometr231 Lt BT;GeoSlab 703 Lt BT;GeoSlab 703 XBd BT;Gigi;Gill Sans;Gill Sans MT;Gill Sans MT Condensed;Gill Sans MT Ext Condensed Bold;Gill Sans Ultra Bold;Gill Sans Ultra Bold Condensed;Gisha;Gloucester MT Extra Condensed;GOTHAM;GOTHAM BOLD;Goudy Old Style;Goudy Stout;GoudyHandtooled BT;GoudyOLSt BT;Gujarati Sangam MN;Gulim;GulimChe;Gungsuh;GungsuhChe;Gurmukhi MN;Haettenschweiler;Harlow Solid Italic;Harrington;Heather;Heiti SC;Heiti TC;HELV;Herald;High Tower Text;Hiragino Kaku Gothic ProN;Hiragino Mincho ProN;Hoefler Text;Humanst 521 Cn BT;Humanst521 BT;Humanst521 Lt BT;Imprint MT Shadow;Incised901 Bd BT;Incised901 BT;Incised901 Lt BT;INCONSOLATA;Informal Roman;Informal011 BT;INTERSTATE;IrisUPC;Iskoola Pota;JasmineUPC;Jazz LET;Jenson;Jester;Jokerman;Juice ITC;Kabel Bk BT;Kabel Ult BT;Kailasa;KaiTi;Kalinga;Kannada Sangam MN;Kartika;Kaufmann Bd BT;Kaufmann BT;Khmer UI;KodchiangUPC;Kokila;Korinna BT;Kristen ITC;Krungthep;Kunstler Script;Lao UI;Latha;Leelawadee;Letter Gothic;Levenim MT;LilyUPC;Lithograph;Lithograph Light;Long Island;Lydian BT;Magneto;Maiandra GD;Malayalam Sangam MN;Malgun Gothic;Mangal;Marigold;Marion;Marker Felt;Market;Marlett;Matisse ITC;Matura MT Script Capitals;Meiryo;Meiryo UI;Microsoft Himalaya;Microsoft JhengHei;Microsoft New Tai Lue;Microsoft PhagsPa;Microsoft Tai Le;Microsoft Uighur;Microsoft YaHei;Microsoft Yi Baiti;MingLiU;MingLiU_HKSCS;MingLiU_HKSCS-ExtB;MingLiU-ExtB;Minion;Minion Pro;Miriam;Miriam Fixed;Mistral;Modern;Modern No. 20;Mona Lisa Solid ITC TT;Mongolian Baiti;MONO;MoolBoran;Mrs Eaves;MS LineDraw;MS Mincho;MS PMincho;MS Reference Specialty;MS UI Gothic;MT Extra;MUSEO;MV Boli;Nadeem;Narkisim;NEVIS;News Gothic;News GothicMT;NewsGoth BT;Niagara Engraved;Niagara Solid;Noteworthy;NSimSun;Nyala;OCR A Extended;Old Century;Old English Text MT;Onyx;Onyx BT;OPTIMA;Oriya Sangam MN;OSAKA;OzHandicraft BT;Palace Script MT;Papyrus;Parchment;Party LET;Pegasus;Perpetua;Perpetua Titling MT;PetitaBold;Pickwick;Plantagenet Cherokee;Playbill;PMingLiU;PMingLiU-ExtB;Poor Richard;Poster;PosterBodoni BT;PRINCETOWN LET;Pristina;PTBarnum BT;Pythagoras;Raavi;Rage Italic;Ravie;Ribbon131 Bd BT;Rockwell;Rockwell Condensed;Rockwell Extra Bold;Rod;Roman;Sakkal Majalla;Santa Fe LET;Savoye LET;Sceptre;Script;Script MT Bold;SCRIPTINA;Serifa;Serifa BT;Serifa Th BT;ShelleyVolante BT;Sherwood;Shonar Bangla;Showcard Gothic;Shruti;Signboard;SILKSCREEN;SimHei;Simplified Arabic;Simplified Arabic Fixed;SimSun;SimSun-ExtB;Sinhala Sangam MN;Sketch Rockwell;Skia;Small Fonts;Snap ITC;Snell Roundhand;Socket;Souvenir Lt BT;Staccato222 BT;Steamer;Stencil;Storybook;Styllo;Subway;Swis721 BlkEx BT;Swiss911 XCm BT;Sylfaen;Synchro LET;System;Tamil Sangam MN;Technical;Teletype;Telugu Sangam MN;Tempus Sans ITC;Terminal;Thonburi;Traditional Arabic;Trajan;TRAJAN PRO;Tristan;Tubular;Tunga;Tw Cen MT;Tw Cen MT Condensed;Tw Cen MT Condensed Extra Bold;TypoUpright BT;Unicorn;Univers;Univers CE 55 Medium;Univers Condensed;Utsaah;Vagabond;Vani;Vijaya;Viner Hand ITC;VisualUI;Vivaldi;Vladimir Script;Vrinda;Westminster;WHITNEY;Wide Latin;ZapfEllipt BT;ZapfHumnst BT;ZapfHumnst Dm BT;Zapfino;Zurich BlkEx BT;Zurich Ex BT;ZWAdobeF".split(";");
                c.options.extendedJsFonts && (f = f.concat(g));
                for (var f = f.concat(c.options.userDefinedFonts), g = u.getElementsByTagName("body")[0], l = u.createElement("div"), k = u.createElement("div"), n = {}, t = {}, h = [], m = 0, p = e.length; m < p; m++) {
                    var q = d();
                    q.style.fontFamily = e[m];
                    l.appendChild(q);
                    h.push(q)
                }
                g.appendChild(l);
                m = 0;
                for (p = e.length; m < p; m++) n[e[m]] = h[m].offsetWidth, t[e[m]] = h[m].offsetHeight;
                h = {};
                m = 0;
                for (p = f.length; m < p; m++) {
                    for (var q = [], r = 0, v = e.length; r < v; r++) {
                        var C;
                        C = f[m];
                        var z = e[r],
                            B = d();
                        B.style.fontFamily = "'" + C + "'," + z;
                        C = B;
                        k.appendChild(C);
                        q.push(C)
                    }
                    h[f[m]] = q
                }
                g.appendChild(k);
                m = [];
                p = 0;
                for (q = f.length; p < q; p++) {
                    r = h[f[p]];
                    v = !1;
                    for (C = 0; C < e.length && !(v = r[C].offsetWidth !== n[e[C]] || r[C].offsetHeight !== t[e[C]]); C++);
                    v && m.push(f[p])
                }
                g.removeChild(k);
                g.removeChild(l);
                a.push({
                    value: m,
                    key: "js_fonts"
                });
                b(a)
            }, 1)
        },
        isWebGlSupported: function() {
            if (!this.isCanvasSupported()) return !1;
            var a = u.createElement("canvas"),
                b;
            try {
                b = a.getContext && (a.getContext("webgl") || a.getContext("experimental-webgl"))
            } catch (c) {
                b = !1
            }
            return !!r.WebGLRenderingContext && !! b
        },
        getHasLiedResolution: function() {
            return y.width < y.availWidth || y.height < y.availHeight ? !0 : !1
        }
    };
    var fb = {
        scrAvailHeight: "88tV",
        flashVersion: "dzuS",
        jsFonts: "EOQP",
        cpuClass: "Md7A",
        adblock: "FMQw",
        javaEnabled: "yD16",
        plugins: "ks0Q",
        openDatabase: "V8vl",
        cookieEnabled: "VPIf",
        browserName: "-UVA",
        storeDb: "Fvje",
        historyList: "kU5z",
        sessionStorage: "HVia",
        systemLanguage: "e6OK",
        hasLiedBrowser: "2xC5",
        scrAvailSize: "TeRS",
        userAgent: "0aew",
        hasLiedOs: "ci5c",
        scrHeight: "5Jwy",
        userLanguage: "hLzX",
        indexedDb: "3sw-",
        touchSupport: "wNLf",
        scrColorDepth: "qmyu",
        srcScreenSize: "tOHY",
        scrAvailWidth: "E-lJ",
        appcodeName: "qT7b",
        browserVersion: "d435",
        os: "hAqN",
        hasLiedResolution: "3neK",
        cookieCode: "VySQ",
        hasLiedLanguages: "j5po",
        webSmartID: "E3gR",
        mimeTypes: "jp76",
        timeZone: "q5aJ",
        scrWidth: "ssI5",
        online: "9vyE",
        localStorage: "XM7l",
        scrDeviceXDPI: "3jCe",
        browserLanguage: "q4f3",
        appMinorVersion: "qBVW",
        localCode: "lEnu",
        doNotTrack: "VEek"
    };
    Array.prototype.indexOf || (Array.prototype.indexOf = function(a, b) {
        var c;
        if (null == this) throw new TypeError('"this" is null or not defined');
        var d = Object(this),
            e = d.length >>> 0;
        if (0 === e) return -1;
        c = +b || 0;
        Infinity === Math.abs(c) && (c = 0);
        if (c >= e) return -1;
        for (c = Math.max(0 <= c ? c : e - Math.abs(c), 0); c < e;) {
            if (c in d && d[c] === a) return c;
            c++
        }
        return -1
    });
    var Y, J = r,
        q = u,
        O = k,
        gb = !1,
        na = [function() {
            if (gb) {
                var a = q.getElementsByTagName("body")[0],
                    b = q.createElement("object");
                b.setAttribute("type", "application/x-shockwave-flash");
                var c = a.appendChild(b);
                if (c) {
                    var d = 0;
                    (function f() {
                        if ("undefined" != typeof c.GetVariable) {
                            var g = c.GetVariable("$version");
                            g && (g = g.split(" ")[1].split(","),
                                n.pv = [parseInt(g[0], 10), parseInt(g[1], 10), parseInt(g[2], 10)])
                        } else if (10 > d) {
                            d++;
                            H(f, 10);
                            return
                        }
                        a.removeChild(b);
                        c = null;
                        Ga()
                    })()
                } else Ga()
            } else Ga()
        }],
        M = [],
        ka = [],
        U = [],
        Z, ma, Da, Ua, Q = !1,
        la = !1,
        L, za, Ra = !0,
        n, Ab = "undefined" != typeof q.getElementById && "undefined" != typeof q.getElementsByTagName && "undefined" != typeof q.createElement,
        ua = O.userAgent.toLowerCase(),
        va = O.platform.toLowerCase(),
        Bb = va ? /win/.test(va) : /win/.test(ua),
        Cb = va ? /mac/.test(va) : /mac/.test(ua),
        Db = /webkit/.test(ua) ? parseFloat(ua.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,
            "$1")) : !1,
        La = !+"\x0B1",
        ga = [0, 0, 0],
        I = null;
    if ("undefined" != typeof O.plugins && "object" == typeof O.plugins["Shockwave Flash"])!(I = O.plugins["Shockwave Flash"].description) || "undefined" != typeof O.mimeTypes && O.mimeTypes["application/x-shockwave-flash"] && !O.mimeTypes["application/x-shockwave-flash"].enabledPlugin || (gb = !0, La = !1, I = I.replace(/^.*\s+(\S+\s+\S+$)/, "$1"), ga[0] = parseInt(I.replace(/^(.*)\..*$/, "$1"), 10), ga[1] = parseInt(I.replace(/^.*\.(.*)\s.*$/, "$1"), 10), ga[2] = /[a-zA-Z]/.test(I) ? parseInt(I.replace(/^.*[a-zA-Z]+(.*)$/,
        "$1"), 10) : 0);
    else if ("undefined" != typeof J.ActiveXObject) try {
        if (I = (new ActiveXObject("ShockwaveFlash.ShockwaveFlash")).GetVariable("$version")) La = !0, I = I.split(" ")[1].split(","), ga = [parseInt(I[0], 10), parseInt(I[1], 10), parseInt(I[2], 10)]
    } catch (a) {}
    n = {
        win: Bb,
        ie: La,
        wk: Db,
        mac: Cb,
        w3: Ab,
        pv: ga
    };
    n.w3 && (("undefined" != typeof q.readyState && "complete" == q.readyState || "undefined" == typeof q.readyState && (q.getElementsByTagName("body")[0] || q.body)) && V(), Q || ("undefined" != typeof q.addEventListener && q.addEventListener("DOMContentLoaded",
        V, !1), n.ie && n.win && (q.attachEvent("onreadystatechange", function b() {
        "complete" == q.readyState && (q.detachEvent("onreadystatechange", b), V())
    }), J == rb && function c() {
        if (!Q) {
            try {
                q.documentElement.doScroll("left")
            } catch (d) {
                H(c, 0);
                return
            }
            V()
        }
    }()), n.wk && function b() {
        Q || (/loaded|complete/.test(q.readyState) ? V() : H(b, 0))
    }(), Va(V)));
    n.ie && n.win && r.attachEvent("onunload", function() {
        for (var a = U.length, b = 0; b < a; b++) U[b][0].detachEvent(U[b][1], U[b][2]);
        a = ka.length;
        for (b = 0; b < a; b++) Ta(ka[b]);
        for (var c in n) n[c] = null;
        n = null;
        for (var d in Y) Y[d] = null;
        Y = null
    });
    Y = {
        getObjectById: function(a) {
            if (n.w3) return Fa(a)
        },
        ua: n,
        addDomLoadEvent: Wa,
        registerObject: function(a, b, c, d) {
            if (n.w3 && a && b) {
                var e = {};
                e.id = a;
                e.swfVersion = b;
                e.expressInstall = c;
                e.callbackFn = d;
                M[M.length] = e;
                P(a, !1)
            } else d && d({
                success: !1,
                id: a
            })
        },
        removeSWF: function(a) {
            n.w3 && Ta(a)
        },
        addLoadEvent: Va,
        createCSS: function(a, b, c, d) {
            n.w3 && Sa(a, b, c, d)
        },
        showExpressInstall: function(a, b, c, d) {
            n.w3 && Ea() && Ca(a, b, c, d)
        },
        hasFlashPlayerVersion: ja,
        createSWF: function(a, b, c) {
            if (n.w3) return Aa(a,
                b, c)
        },
        getFlashPlayerVersion: function() {
            return {
                release: n.pv[2],
                minor: n.pv[1],
                major: n.pv[0]
            }
        },
        embedSWF: function(a, b, c, d, e, f, g, l, k, p) {
            var t = {
                success: !1,
                id: b
            };
            n.w3 && !(n.wk && 312 > n.wk) && a && b && c && d && e ? (P(b, !1), Wa(function() {
                c += "";
                d += "";
                var h = {};
                if (k && "object" === typeof k) for (var m in k) h[m] = k[m];
                h.data = a;
                h.width = c;
                h.height = d;
                m = {};
                if (l && "object" === typeof l) for (var n in l) m[n] = l[n];
                if (g && "object" === typeof g) for (var q in g) m.flashvars = "undefined" != typeof m.flashvars ? m.flashvars + ("\x26" + q + "\x3d" + g[q]) : q + "\x3d" + g[q];
                if (ja(e)) n = Aa(h, m, b), h.id == b && P(b, !0), t.success = !0, t.ref = n;
                else {
                    if (f && Ea()) {
                        h.data = f;
                        Ca(h, m, b, p);
                        return
                    }
                    P(b, !0)
                }
                p && p(t)
            })) : p && p(t)
        },
        expressInstallCallback: function() {
            if (la) {
                var a = K("SWFObjectExprInst");
                a && Z && (a.parentNode.replaceChild(Z, a), ma && (P(ma, !0), n.ie && n.win && (Z.style.display = "block")), Da && Da(Ua));
                la = !1
            }
        },
        switchOffAutoHideShow: function() {
            Ra = !1
        },
        getQueryParamValue: function(a) {
            var b = q.location.search || q.location.hash;
            if (b) {
                /\?/.test(b) && (b = b.split("?")[1]);
                if (null == a) return Qa(b);
                for (var b = b.split("\x26"), c = 0; c < b.length; c++) if (b[c].substring(0, b[c].indexOf("\x3d")) == a) return Qa(b[c].substring(b[c].indexOf("\x3d") + 1))
            }
            return ""
        }
    };
    aa.VERSION = "1.4.2";
    var Eb = ["scrDeviceXDPI", "scrColorDepth", "scrWidth", "scrHeight"],
        Fb = ["sessionStorage", "localStorage", "indexedDb", "openDatabase"],
        Gb = ["scrAvailWidth", "scrAvailHeight"],
        qb = 0,
        Hb = "appCodeName appMinorVersion appName cpuClass onLine systemLanguage userLanguage historyList hasLiedLanguages hasLiedResolution hasLiedOs hasLiedBrowser".split(" ");
    try {
        var w = r,
            x = w.document,
            hb = w.Image,
            Ma = w.globalStorage,
            ib = w.swfobject;
        try {
            var Na = w.localStorage
        } catch (a) {}
        try {
            var Oa = w.sessionStorage
        } catch (a) {}
        var ha, wa, jb = {
            pngCookieName: "evercookie_png",
            cacheCookieName: "evercookie_cache",
            etagCookieName: "evercookie_etag",
            history: !1,
            phpuri: "/php",
            cachePath: "/evercookie_cache.php",
            pngPath: "/evercookie_png.php",
            asseturi: "/assets",
            baseurl: "",
            tests: 2,
            silverlight: !1,
            etagPath: "/evercookie_etag.php",
            java: !1,
            authPath: !1,
            domain: ya(w.location.host.split(":")[0])
        };
        w._evercookie_flash_var = function(a) {
            ha = a;
            (a = x.getElementById("myswf")) && a.parentNode && a.parentNode.removeChild(a)
        };
        w.evercookie = w.Evercookie = function(a) {
            a = a || {};
            var b = {}, c;
            for (c in jb) {
                var d = a[c];
                b[c] = "undefined" !== typeof d ? d : jb[c]
            }
            "function" === typeof b.domain && (b.domain = b.domain(w));
            var e = b.history,
                f = b.java,
                g = b.tests,
                l = b.baseurl,
                k = b.asseturi,
                p = b.phpuri,
                n = b.domain,
                h = this;
            this._ec = {};
            this.get = function(a, b, c) {
                h._evercookie(a, b, void 0, void 0, c)
            };
            this.set = function(a, b) {
                h._evercookie(a, function() {}, b)
            };
            this._evercookie = function(a,
                                        c, d, B, l) {
                void 0 === h._evercookie && (h = this);
                void 0 === B && (B = 0);
                0 === B && (h.evercookie_database_storage(a, d), h.evercookie_indexdb_storage(a, d), b.authPath && h.evercookie_auth(a, d), f && h.evercookie_java(a, d), h._ec.userData = h.evercookie_userdata(a, d), h._ec.cookieData = h.evercookie_cookie(a, d), h._ec.localData = h.evercookie_local_storage(a, d), h._ec.globalData = h.evercookie_global_storage(a, d), h._ec.sessionData = h.evercookie_session_storage(a, d), h._ec.windowData = h.evercookie_window(a, d), e && (h._ec.historyData = h.evercookie_history(a,
                    d)));
                if (void 0 !== d)("undefined" === typeof ha || "undefined" === typeof wa) && B++ < g && H(function() {
                    h._evercookie(a, c, d, B, l)
                }, 300);
                else if ((w.openDatabase && "undefined" === typeof h._ec.dbData || ("indexedDB" in w || (w.indexedDB = w.indexedDB || w.mozIndexedDB || w.webkitIndexedDB || w.msIndexedDB)) && ("undefined" === typeof h._ec.idbData || "" === h._ec.idbData) || "undefined" === typeof ha || "undefined" === typeof h._ec.etagData || "undefined" === typeof h._ec.cacheData || "undefined" === typeof h._ec.javaData || x.createElement("canvas").getContext && ("undefined" === typeof h._ec.pngData || "" === h._ec.pngData) || "undefined" === typeof wa) && B++ < g) H(function() {
                    h._evercookie(a, c, d, B, l)
                }, 20);
                else {
                    h._ec.lsoData = h.getFromStr(a, ha);
                    ha = void 0;
                    h._ec.slData = h.getFromStr(a, wa);
                    wa = void 0;
                    var v = h._ec,
                        z = [],
                        k = 0,
                        C, m;
                    h._ec = {};
                    for (m in v) v[m] && "null" !== v[m] && "undefined" !== v[m] && (z[v[m]] = void 0 === z[v[m]] ? 1 : z[v[m]] + 1);
                    for (m in z) z[m] > k && (k = z[m], C = m);
                    void 0 === C || void 0 !== l && 1 === l || h.set(a, C);
                    "function" === typeof c && c(C, v)
                }
            };
            this.evercookie_window = function(a, b) {
                try {
                    if (void 0 !== b) {
                        var c;
                        var d = w.name;
                        if (-1 < d.indexOf("\x26" + a + "\x3d") || 0 === d.indexOf(a + "\x3d")) {
                            var e = d.indexOf("\x26" + a + "\x3d"),
                                f; - 1 === e && (e = d.indexOf(a + "\x3d"));
                            f = d.indexOf("\x26", e + 1);
                            c = -1 !== f ? d.substr(0, e) + d.substr(f + (e ? 0 : 1)) + "\x26" + a + "\x3d" + b : d.substr(0, e) + "\x26" + a + "\x3d" + b
                        } else c = d + "\x26" + a + "\x3d" + b;
                        w.name = c
                    } else return this.getFromStr(a, w.name)
                } catch (Ib) {}
            };
            this.evercookie_userdata = function(a, b) {
                try {
                    var c = this.createElem("div", "userdata_el", 1);
                    if (c.addBehavior) if (c.style.behavior = "url(#default#userData)",
                    void 0 !== b) c.setAttribute(a, b), c.save(a);
                    else return c.load(a), c.getAttribute(a)
                } catch (B) {}
            };
            this.evercookie_cache = function(a, c) {
                if (void 0 !== c) x.cookie = b.cacheCookieName + "\x3d" + c + "; path\x3d/; domain\x3d" + n, h.ajax({
                    url: l + p + b.cachePath + "?name\x3d" + a + "\x26cookie\x3d" + b.cacheCookieName,
                    success: function() {}
                });
                else {
                    var d = this.getFromStr(b.cacheCookieName, x.cookie);
                    h._ec.cacheData = void 0;
                    x.cookie = b.cacheCookieName + "\x3d; expires\x3dMon, 20 Sep 2010 00:00:00 UTC; path\x3d/; domain\x3d" + n;
                    h.ajax({
                        url: l + p + b.cachePath + "?name\x3d" + a + "\x26cookie\x3d" + b.cacheCookieName,
                        success: function(a) {
                            x.cookie = b.cacheCookieName + "\x3d" + d + "; expires\x3dTue, 31 Dec 2030 00:00:00 UTC; path\x3d/; domain\x3d" + n;
                            h._ec.cacheData = a
                        }
                    })
                }
            };
            this.evercookie_auth = function(a, c) {
                if (void 0 !== c) {
                    var d = "//" + c + "@" + sb.host + l + p + b.authPath + "?name\x3d" + a,
                        e = new hb;
                    e.style.visibility = "hidden";
                    e.style.position = "absolute";
                    e.src = d
                } else h.ajax({
                    success: function(a) {
                        h._ec.authData = a
                    },
                    url: l + p + b.authPath + "?name\x3d" + a
                })
            };
            this.evercookie_etag = function(a,
                                            c) {
                if (void 0 !== c) x.cookie = b.etagCookieName + "\x3d" + c + "; path\x3d/; domain\x3d" + n, h.ajax({
                    success: function() {},
                    url: l + p + b.etagPath + "?name\x3d" + a + "\x26cookie\x3d" + b.etagCookieName
                });
                else {
                    var d = this.getFromStr(b.etagCookieName, x.cookie);
                    h._ec.etagData = void 0;
                    x.cookie = b.etagCookieName + "\x3d; expires\x3dMon, 20 Sep 2010 00:00:00 UTC; path\x3d/; domain\x3d" + n;
                    h.ajax({
                        url: l + p + b.etagPath + "?name\x3d" + a + "\x26cookie\x3d" + b.etagCookieName,
                        success: function(a) {
                            x.cookie = b.etagCookieName + "\x3d" + d + "; expires\x3dTue, 31 Dec 2030 00:00:00 UTC; path\x3d/; domain\x3d" + n;
                            h._ec.etagData = a
                        }
                    })
                }
            };
            this.evercookie_java = function(a, b) {
                function c(c) {
                    c = x.getElementById(c);
                    void 0 !== b ? c.set(a, b) : h._ec.javaData = c.get(a)
                }
                var d = x.getElementById("ecAppletContainer");
                "undefined" !== typeof dtjava && (null !== d && void 0 !== d && d.length || (d = x.createElement("div"), d.setAttribute("id", "ecAppletContainer"), d.style.position = "absolute", d.style.top = "-3000px", d.style.left = "-3000px", d.style.width = "1px", d.style.height = "1px", x.body.appendChild(d)), "undefined" === typeof ecApplet ? dtjava.embed({
                    id: "ecApplet",
                    height: "1px",
                    width: "1px",
                    placeholder: "ecAppletContainer",
                    url: l + k + "/evercookie.jnlp"
                }, {}, {
                    onJavascriptReady: c
                }) : c("ecApplet"))
            };
            this.evercookie_lso = function(a, b) {
                var c = x.getElementById("swfcontainer"),
                    d = {}, e = {}, f = {};
                null !== c && void 0 !== c && c.length || (c = x.createElement("div"), c.setAttribute("id", "swfcontainer"), x.body.appendChild(c));
                void 0 !== b && (d.everdata = a + "\x3d" + b);
                e.swliveconnect = "true";
                f.id = "myswf";
                f.name = "myswf";
                ib.embedSWF(l + k + "/evercookie.swf", "swfcontainer", "1", "1", "9.0.0", !1, d, e, f)
            };
            this.evercookie_png = function(a, c) {
                var d = x.createElement("canvas"),
                    e, f, g;
                d.style.visibility = "hidden";
                d.style.position = "absolute";
                d.width = 200;
                d.height = 1;
                d && d.getContext && (e = new hb, e.style.visibility = "hidden", e.style.position = "absolute", void 0 !== c ? x.cookie = b.pngCookieName + "\x3d" + c + "; path\x3d/; domain\x3d" + n : (h._ec.pngData = void 0, f = d.getContext("2d"), g = this.getFromStr(b.pngCookieName, x.cookie), x.cookie = b.pngCookieName + "\x3d; expires\x3dMon, 20 Sep 2010 00:00:00 UTC; path\x3d/; domain\x3d" + n, e.onload = function() {
                    x.cookie = b.pngCookieName + "\x3d" + g + "; expires\x3dTue, 31 Dec 2030 00:00:00 UTC; path\x3d/; domain\x3d" + n;
                    h._ec.pngData = "";
                    f.drawImage(e, 0, 0);
                    var a = f.getImageData(0, 0, 200, 1).data,
                        c, d;
                    c = 0;
                    for (d = a.length; c < d && 0 !== a[c]; c += 4) {
                        h._ec.pngData += String.fromCharCode(a[c]);
                        if (0 === a[c + 1]) break;
                        h._ec.pngData += String.fromCharCode(a[c + 1]);
                        if (0 === a[c + 2]) break;
                        h._ec.pngData += String.fromCharCode(a[c + 2])
                    }
                }), e.src = l + p + b.pngPath + "?name\x3d" + a + "\x26cookie\x3d" + b.pngCookieName)
            };
            this.evercookie_local_storage = function(a, b) {
                try {
                    if (Na) if (void 0 !== b) Na.setItem(a, b);
                    else return Na.getItem(a)
                } catch (z) {}
            };
            this.evercookie_database_storage = function(a, b) {
                try {
                    if (w.openDatabase) {
                        var c = w.openDatabase("sqlite_evercookie", "", "evercookie", 1048576);
                        void 0 !== b ? c.transaction(function(c) {
                            c.executeSql("CREATE TABLE IF NOT EXISTS cache(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, value TEXT NOT NULL, UNIQUE (name))", [], function() {}, function() {});
                            c.executeSql("INSERT OR REPLACE INTO cache(name, value) VALUES(?, ?)", [a, b], function() {}, function() {})
                        }) : c.transaction(function(b) {
                            b.executeSql("SELECT value FROM cache WHERE name\x3d?", [a], function(a, b) {
                                h._ec.dbData = 1 <= b.rows.length ? b.rows.item(0).value : ""
                            }, function() {})
                        })
                    }
                } catch (B) {}
            };
            this.evercookie_indexdb_storage = function(a, b) {
                try {
                    if ("indexedDB" in w || (indexedDB = w.indexedDB || w.mozIndexedDB || w.webkitIndexedDB || w.msIndexedDB, IDBTransaction = w.IDBTransaction || w.webkitIDBTransaction || w.msIDBTransaction, IDBKeyRange = w.IDBKeyRange || w.webkitIDBKeyRange || w.msIDBKeyRange), indexedDB) {
                        var c = indexedDB.open("idb_evercookie",
                            1);
                        c.onerror = function() {};
                        c.onupgradeneeded = function(a) {
                            a.target.result.createObjectStore("evercookie", {
                                keyPath: "name",
                                unique: !1
                            })
                        };
                        c.onsuccess = void 0 !== b ? function(c) {
                            c = c.target.result;
                            c.objectStoreNames.contains("evercookie") && c.transaction(["evercookie"], "readwrite").objectStore("evercookie").put({
                                name: a,
                                value: b
                            });
                            c.close()
                        } : function(b) {
                            b = b.target.result;
                            if (b.objectStoreNames.contains("evercookie")) {
                                var c = b.transaction(["evercookie"]).objectStore("evercookie").get(a);
                                c.onsuccess = function() {
                                    h._ec.idbData = void 0 === c.result ? void 0 : c.result.value
                                }
                            } else h._ec.idbData = void 0;
                            b.close()
                        }
                    }
                } catch (B) {}
            };
            this.evercookie_session_storage = function(a, b) {
                try {
                    if (Oa) if (void 0 !== b) Oa.setItem(a, b);
                    else return Oa.getItem(a)
                } catch (z) {}
            };
            this.evercookie_global_storage = function(a, b) {
                if (Ma) {
                    var c = this.getHost();
                    try {
                        if (void 0 !== b) Ma[c][a] = b;
                        else return Ma[c][a]
                    } catch (B) {}
                }
            };
            this.encode = function(a) {
                var b = "",
                    c, d, e, f, g, h, l = 0;
                for (a = this._utf8_encode(a); l < a.length;) c = a.charCodeAt(l++), d = a.charCodeAt(l++), e = a.charCodeAt(l++), f = c >> 2, c = (c & 3) << 4 | d >> 4, g = (d & 15) << 2 | e >> 6, h = e & 63, isNaN(d) ? g = h = 64 : isNaN(e) && (h = 64), b = b + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".charAt(f) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".charAt(c) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".charAt(g) + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".charAt(h);
                return b
            };
            this.decode = function(a) {
                var b = "",
                    c, d, e, f, g, h = 0;
                for (a = a.replace(/[^A-Za-z0-9\+\/\=]/g,
                    ""); h < a.length;) c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".indexOf(a.charAt(h++)), d = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".indexOf(a.charAt(h++)), f = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".indexOf(a.charAt(h++)), g = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d".indexOf(a.charAt(h++)), c = c << 2 | d >> 4, d = (d & 15) << 4 | f >> 2, e = (f & 3) << 6 | g, b += String.fromCharCode(c), 64 !== f && (b += String.fromCharCode(d)),
                64 !== g && (b += String.fromCharCode(e));
                return b = this._utf8_decode(b)
            };
            this._utf8_encode = function(a) {
                a = a.replace(/\r\n/g, "\n");
                for (var b = "", c = 0, d = a.length, e; c < d; c++) e = a.charCodeAt(c), 128 > e ? b += String.fromCharCode(e) : (127 < e && 2048 > e ? b += String.fromCharCode(e >> 6 | 192) : (b += String.fromCharCode(e >> 12 | 224), b += String.fromCharCode(e >> 6 & 63 | 128)), b += String.fromCharCode(e & 63 | 128));
                return b
            };
            this._utf8_decode = function(a) {
                for (var b = "", c = 0, d = a.length, e, f, g; c < d;) e = a.charCodeAt(c), 128 > e ? (b += String.fromCharCode(e), c += 1) : 191 < e && 224 > e ? (f = a.charCodeAt(c + 1), b += String.fromCharCode((e & 31) << 6 | f & 63), c += 2) : (f = a.charCodeAt(c + 1), g = a.charCodeAt(c + 2), b += String.fromCharCode((e & 15) << 12 | (f & 63) << 6 | g & 63), c += 3);
                return b
            };
            this.evercookie_history = function(a, b) {
                var c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\x3d-".split(""),
                    d = "" + this.getHost() + "/" + a,
                    e, f = "",
                    g = "",
                    h = 1;
                if (void 0 !== b) {
                    if (!this.hasVisited(d)) {
                        this.createIframe(d, "if");
                        d += "/";
                        c = this.encode(b).split("");
                        for (e = 0; e < c.length; e++) d += c[e], this.createIframe(d,
                            "if" + e);
                        this.createIframe(d + "-", "if_")
                    }
                } else if (this.hasVisited(d)) {
                    for (d += "/";
                         "-" !== f && 1 === h;) for (e = h = 0; e < c.length; e++) if (this.hasVisited(d + c[e])) {
                        f = c[e];
                        "-" !== f && (g += f);
                        d += f;
                        h = 1;
                        break
                    }
                    return this.decode(g)
                }
            };
            this.createElem = function(a, b, c) {
                a = void 0 !== b && x.getElementById(b) ? x.getElementById(b) : x.createElement(a);
                a.style.visibility = "hidden";
                a.style.position = "absolute";
                b && a.setAttribute("id", b);
                c && x.body.appendChild(a);
                return a
            };
            this.createIframe = function(a, b) {
                this.createElem("iframe", b, 1).setAttribute("src",
                    a)
            };
            var m = this.waitForSwf = function(a) {
                void 0 === a ? a = 0 : a++;
                a < g && "undefined" === typeof ib && H(function() {
                    m(a)
                }, 300)
            };
            this.evercookie_cookie = function(a, b) {
                if (void 0 !== b) x.cookie = a + "\x3d" + b + "; expires\x3dTue, 31 Dec 2030 00:00:00 UTC; path\x3d/; domain\x3d" + n;
                else return this.getFromStr(a, x.cookie)
            };
            this.getFromStr = function(a, b) {
                if ("string" === typeof b) {
                    var c = a + "\x3d",
                        d = b.split(/[;&]/),
                        e, f;
                    for (e = 0; e < d.length; e++) {
                        for (f = d[e];
                             " " === f.charAt(0);) f = f.substring(1, f.length);
                        if (0 === f.indexOf(c)) return f.substring(c.length,
                            f.length)
                    }
                }
            };
            this.getHost = function() {
                return ya(w.location.host.split(":")[0])
            };
            this.toHex = function(a) {
                for (var b = "", c = a.length, d = 0, e; d < c;) {
                    for (e = a.charCodeAt(d++).toString(16); 2 > e.length;) e = "0" + e;
                    b += e
                }
                return b
            };
            this.fromHex = function(a) {
                for (var b = "", c = a.length, d; 0 <= c;) d = c - 2, b = String.fromCharCode("0x" + a.substring(d, c)) + b, c = d;
                return b
            };
            this.hasVisited = function(a) {
                -1 === this.no_color && -1 === this._getRGB("", -1) && (this.no_color = this._getRGB("" + Math.floor(9999999 * Math.random()) + "rand.com"));
                return 0 === a.indexOf("https:") || 0 === a.indexOf("http:") ? this._testURL(a, this.no_color) : this._testURL("http://" + a, this.no_color) || this._testURL("https://" + a, this.no_color) || this._testURL("http://www." + a, this.no_color) || this._testURL("https://www." + a, this.no_color)
            };
            var q = this.createElem("a", "_ec_rgb_link"),
                r, u;
            try {
                r = 1, u = x.createElement("style"), u.styleSheet ? u.styleSheet.innerHTML = "#_ec_rgb_link:visited{display:none;color:#FF0000}" : u.innerHTML ? u.innerHTML = "#_ec_rgb_link:visited{display:none;color:#FF0000}" : u.appendChild(x.createTextNode("#_ec_rgb_link:visited{display:none;color:#FF0000}"))
            } catch (v) {
                r = 0
            }
            this._getRGB = function(a, b) {
                if (b && 0 === r) return -1;
                q.href = a;
                q.innerHTML = a;
                x.body.appendChild(u);
                x.body.appendChild(q);
                var c;
                if (x.defaultView) {
                    if (null == x.defaultView.getComputedStyle(q, null)) return -1;
                    c = x.defaultView.getComputedStyle(q, null).getPropertyValue("color")
                } else c = q.currentStyle.color;
                return c
            };
            this._testURL = function(a, b) {
                var c = this._getRGB(a);
                return "rgb(255, 0, 0)" === c || "#ff0000" === c || b && c !== b ? 1 : 0
            }
        }
    } catch (a) {}
    var kb = {
        postMessage: function(a, b) {
            b = b || tb;
            if (b.postMessage) b.postMessage(a, "*");
            else if (a && "function" == typeof k.onData) k.onData(a)
        }
    };
    ia.prototype = {
        getDfpMoreInfo: function(a) {
            var b = this;
            this.moreInfoArray = [];
            b.cfp.get(function(c, d) {
                b.moreInfoArray.push(b.getCanvansCode(c + ""));
                for (var e in d) {
                    c = d[e].key;
                    var f = d[e].value + "";
                    switch (c) {
                        case "session_storage":
                            b.moreInfoArray.push(b.getSessionStorage(f));
                            break;
                        case "local_storage":
                            b.moreInfoArray.push(b.getLocalStorage(f));
                            break;
                        case "indexed_db":
                            b.moreInfoArray.push(b.getIndexedDb(f));
                            break;
                        case "open_database":
                            b.moreInfoArray.push(b.getOpenDatabase(f));
                            break;
                        case "do_not_track":
                            b.moreInfoArray.push(b.getDoNotTrack(f));
                            break;
                        case "ie_plugins":
                            b.moreInfoArray.push(b.getPlugins(f));
                            break;
                        case "regular_plugins":
                            b.moreInfoArray.push(b.getPlugins());
                            break;
                        case "adblock":
                            b.moreInfoArray.push(b.getAdblock(f));
                            break;
                        case "has_lied_languages":
                            b.moreInfoArray.push(b.getHasLiedLanguages(f));
                            break;
                        case "has_lied_resolution":
                            b.moreInfoArray.push(b.getHasLiedResolution(f));
                            break;
                        case "has_lied_os":
                            b.moreInfoArray.push(b.getHasLiedOs(f));
                            break;
                        case "has_lied_browser":
                            b.moreInfoArray.push(b.getHasLiedBrowser(f));
                            break;
                        case "touch_support":
                            b.moreInfoArray.push(b.getTouchSupport(f));
                            break;
                        case "js_fonts":
                            b.moreInfoArray.push(b.getJsFonts(f))
                    }
                }
                "function" == typeof a && a()
            })
        },
        checkWapOrWeb: function() {
            return "WindowsPhone" == Ha() || "iOS" == Ha() || "Android" == Ha() ? !0 : !1
        },
        getUUID: function() {
            return "" == G("RAIL_UUID") || null == G("RAIL_UUID") || void 0 == G("RAIL_UUID") ? new p("cookieCode", "new") : new p("UUID", G("RAIL_UUID"))
        },
        getTimeZone: function() {
            var a = (new Date).getTimezoneOffset() / 60;
            return new p("timeZone", a)
        },
        checkBroswer: function() {
            k.userAgent.toString().indexOf("MSIE")
        },
        getFlashVersion: function() {
            var a = 0;
            if ("IE" == this.checkBroswer()) var b = new ActiveXObject("ShockwaveFlash.ShockwaveFlash"),
                a = Number(b.GetVariable("$version").split(" ")[1].replace(/,/g, ".").replace(/^(d+.d+).*$/, "$1"));
            else k.plugins && 0 < k.plugins.length && (b = k.plugins["Shockwave Flash"]) && (flashArr = b.description.split(" "), a = flashArr[2] + " " + flashArr[3]);
            return new p("flashVersion", a)
        },
        getOpenDatabase: function(a) {
            return new p("openDatabase", a)
        },
        getCanvansCode: function(a) {
            var b;
            b = this.checkWapOrWeb() ?
                "wapSmartID" : "webSmartID";
            return new p(b, a)
        },
        getFingerPrint: function() {
            var a = this;
            r.RTCPeerConnection || r.webkitRTCPeerConnection || r.mozRTCPeerConnection ? mb(function(b) {
                a.initEc(b)
            }) : a.initEc()
        },
        getUserLanguage: function() {
            var a;
            a = "IE" == this.checkBroswer() || this.checkOperaBroswer() ? k.userLanguage.toString() : "";
            return new p("userLanguage", a)
        },
        getJavaEnabled: function() {
            return new p("javaEnabled", k.javaEnabled() ? "1" : "0")
        },
        getCookieCode: function() {
            "" == G("RAIL_OkLJUJ") || null == G("RAIL_OkLJUJ") || void 0 == G("RAIL_OkLJUJ") || G("RAIL_OkLJUJ");
            return new p("cookieCode", "new")
        },
        getSendPlatform: function() {
            var a;
            a = this.checkWapOrWeb() ? Ya[1] : Ya[0];
            return new p("platform", a)
        },
        getHistoryList: function() {
            return new p("historyList", r.history.length)
        },
        getTouchSupport: function(a) {
            return new p("touchSupport", ba(a.replace(RegExp(",", "gm"), "#")))
        },
        getPlatform: function() {
            return new p("os", k.platform.toString())
        },
        getSessionStorage: function(a) {
            return new p("sessionStorage", a)
        },
        NeedUpdate: function() {
            W("fp_ver", "4.6.1", 0);
            W("BSFIT_OKLJUJ",
                "", 0);
            return !1
        },
        initEc: function(a) {
            var b = "",
                c = this,
                d = void 0 != a && void 0 != a.localAddr ? a.localAddr : "";
            c.checkWapOrWeb();
            this.ec.get("RAIL_OkLJUJ", function(a) {
                b = a;
                c.getDfpMoreInfo(function() {
                    if (!(9E5 < G("RAIL_EXPIRATION") - (new Date).getTime() & null != G("RAIL_DEVICEID") & void 0 != G("RAIL_DEVICEID") & !c.NeedUpdate())) {
                        for (var a = "", e = "", l = c.getpackStr(b), k = [], n = [], q = [], h = [], m = 0; m < l.length; m++) "new" != l[m].value && -1 == Hb.indexOf(l[m].key) && (-1 != Fb.indexOf(l[m].key) ? n.push(l[m]) : -1 != Gb.indexOf(l[m].key) ? q.push(l[m]) : -1 != Eb.indexOf(l[m].key) ? h.push(l[m]) : k.push(l[m]));
                        l = "";
                        for (m = 0; m < n.length; m++) l = l + n[m].key.charAt(0) + n[m].value;
                        n = "";
                        for (m = 0; m < h.length; m++) n = 0 == m ? n + h[m].value : n + "x" + h[m].value;
                        h = "";
                        for (m = 0; m < q.length; m++) h = 0 == m ? h + q[m].value : h + "x" + q[m].value;
                        k.push(new p("storeDb", l));
                        k.push(new p("srcScreenSize", n));
                        k.push(new p("scrAvailSize", h));
                        "" != d && k.push(new p("localCode", nb(d)));
                        e = c.hashAlg(k, a, e);
                        a = e.key;
                        e = e.value;
                        a += "\x26timestamp\x3d" + (new Date).getTime();
                        Za.getJSON("https://kyfw.12306.cn/otn/HttpZF/logdevice" + ("?algID\x3deN9MhjRjlm\x26hashCode\x3d" + e + a), null, function(a) {
                            var b = JSON.parse(a);
                            void 0 != kb && kb.postMessage(a, r.parent);
                            for (var d in b) "dfp" == d ? G("RAIL_DEVICEID") != b[d] && (W("RAIL_DEVICEID", b[d], 1E3), c.deviceEc.set("RAIL_DEVICEID", b[d])) : "exp" == d ? W("RAIL_EXPIRATION", b[d], 1E3) : "cookieCode" == d && (c.ec.set("RAIL_OkLJUJ", b[d]), W("RAIL_OkLJUJ", "", 0))
                        })
                    }
                })
            }, 1)
        },
        getPlugins: function(a) {
            if ("IE" == this.checkBroswer()) return new p("plugins", ba(a.replace(RegExp(",", "gm"), "#")));
            a = k.plugins;
            var b = "";
            for (i = 0; i < a.length; i++) b += a[i].name.toString() + "#";
            return new p("plugins", ba(b))
        },
        getAppCodeName: function() {
            return new p("appCodeName", k.appCodeName.toString())
        },
        getScrHeight: function() {
            return new p("scrHeight", r.screen.height.toString())
        },
        getMachineCode: function() {
            return [this.getUUID(), this.getCookieCode(), this.getUserAgent(), this.getScrHeight(), this.getScrWidth(), this.getScrAvailHeight(), this.getScrAvailWidth(), this.md5ScrColorDepth(), this.getScrDeviceXDPI(), this.getAppCodeName(), this.getAppName(), this.getJavaEnabled(),
                this.getMimeTypes(), this.getPlatform(), this.getAppMinorVersion(), this.getBrowserLanguage(), this.getCookieEnabled(), this.getCpuClass(), this.getOnLine(), this.getSystemLanguage(), this.getUserLanguage(), this.getTimeZone(), this.getFlashVersion(), this.getHistoryList(), this.getCustId(), this.getSendPlatform()]
        },
        getHasLiedLanguages: function(a) {
            return new p("hasLiedLanguages", a)
        },
        getHasLiedResolution: function(a) {
            return new p("hasLiedResolution", a)
        },
        getpackStr: function(a) {
            var b = [],
                b = [],
                b = this.getMachineCode(),
                b = b.concat(this.moreInfoArray);
            null != a && void 0 != a && "" != a && 32 == a.length && b.push(new p("cookieCode", a));
            b.sort(function(a, b) {
                var c, d;
                if ("object" === typeof a && "object" === typeof b && a && b) return c = a.key, d = b.key, c === d ? 0 : typeof c === typeof d ? c < d ? -1 : 1 : typeof c < typeof d ? -1 : 1;
                throw "error";
            });
            return b
        },
        getSystemLanguage: function() {
            var a;
            a = "IE" == this.checkBroswer() || this.checkOperaBroswer() ? k.systemLanguage.toString() : "";
            return new p("systemLanguage", a)
        },
        getScrWidth: function() {
            return new p("scrWidth", r.screen.width.toString())
        },
        getScrDeviceXDPI: function() {
            var a;
            a = "IE" == this.checkBroswer() ? r.screen.deviceXDPI.toString() : "";
            return new p("scrDeviceXDPI", a)
        },
        getOnLine: function() {
            return new p("onLine", k.onLine.toString())
        },
        getAdblock: function(a) {
            return new p("adblock", a)
        },
        md5ScrColorDepth: function() {
            return new p("scrColorDepth", r.screen.colorDepth.toString())
        },
        getScrAvailHeight: function() {
            return new p("scrAvailHeight", r.screen.availHeight.toString())
        },
        getAppName: function() {
            return new p("appName", k.appName.toString())
        },
        getBrowserLanguage: function() {
            var a;
            a = "IE" == this.checkBroswer() || this.checkOperaBroswer() ? k.browserLanguage.toString() : this.getLanguage();
            return new p("browserLanguage", a)
        },
        getLocalStorage: function(a) {
            return new p("localStorage", a)
        },
        checkOperaBroswer: function() {
            return r.opera
        },
        getCpuClass: function() {
            var a;
            a = "IE" == this.checkBroswer() ? k.cpuClass.toString() : "";
            return new p("cpuClass", a)
        },
        getIndexedDb: function(a) {
            return new p("indexedDb", a)
        },
        getScrAvailWidth: function() {
            return new p("scrAvailWidth", r.screen.availWidth.toString())
        },
        hashAlg: function(a, b, c) {
            a.sort(function(a, b) {
                var c, d;
                if ("object" === typeof a && "object" === typeof b && a && b) return c = a.key, d = b.key, c === d ? 0 : typeof c === typeof d ? c < d ? -1 : 1 : typeof c < typeof d ? -1 : 1;
                throw "error";
            });
            for (var d = 0; d < a.length; d++) {
                var e = a[d].key.replace(RegExp("%", "gm"), ""),
                    f = "",
                    f = "string" == typeof a[d].value ? a[d].value.replace(RegExp("%", "gm"), "") : a[d].value;
                "" !== f && (c += e + f, b += "\x26" + (void 0 == fb[e] ? e : fb[e]) + "\x3d" + f)
            }
            a = R.SHA256(c).toString(R.enc.Base64);
            a = xa(a);
            c = a.length;
            d = a.split("");
            for (e = 0; e < parseInt(c / 2); e++) 0 == e % 2 && (f = a.charAt(e), d[e] = d[c - 1 - e], d[c - 1 - e] = f);
            a = d.join("");
            a = xa(a);
            c = xa(a);
            c = R.SHA256(c).toString(R.enc.Base64);
            return new p(b, c)
        },
        getJsFonts: function(a) {
            return new p("jsFonts", ba(a.replace(RegExp(",", "gm"), "#")))
        },
        getUserAgent: function() {
            var a = k.userAgent,
                a = a.replace(/\&|\+/g, "");
            return new p("userAgent", a.toString())
        },
        getCustId: function() {
            return new p("custID", "133")
        },
        getCookieEnabled: function() {
            return new p("cookieEnabled", k.cookieEnabled ? "1" : "0")
        },
        getHasLiedBrowser: function(a) {
            return new p("hasLiedBrowser",
                a)
        },
        getAppMinorVersion: function() {
            var a;
            a = "IE" == this.checkBroswer() ? k.appMinorVersion.toString() : "";
            return new p("appMinorVersion", a)
        },
        getMimeTypes: function() {
            for (var a = k.mimeTypes, b = "", c = 0; c < a.length; c++) b += a[c].type + "#";
            return new p("mimeTypes", ba(b.substr(0, b.length - 1)))
        },
        getLanguage: function() {
            return null != k.language ? k.language.toString() : ""
        },
        getHasLiedOs: function(a) {
            return new p("hasLiedOs", a)
        },
        getDoNotTrack: function(a) {
            return new p("doNotTrack", a)
        }
    };
    var lb = !1;
    u.addEventListener ? u.addEventListener("DOMContentLoaded",

        function b() {
            u.removeEventListener("DOMContentLoaded", b, !1);
            Pa()
        }, !1) : u.attachEvent && u.attachEvent("onreadystatechange", function c() {
        lb || "interactive" != u.readyState && "complete" != u.readyState || (u.detachEvent("onreadystatechange", c), Pa(), lb = !0)
    })
})();
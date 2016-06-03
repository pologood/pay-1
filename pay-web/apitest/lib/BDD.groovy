package lib;

@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
@Grab('org.slf4j:slf4j-simple:1.7.16')
@Grab('com.jayway.jsonpath:json-path:2.2.0')
@Grab(group = 'org.ccil.cowan.tagsoup', module = 'tagsoup', version = '1.2.1')

import groovyx.net.http.URIBuilder
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.BINARY
import com.jayway.jsonpath.JsonPath
import org.ccil.cowan.tagsoup.Parser

class JsonReader {
    def ctx

    def JsonReader(ctx) {
        this.ctx = ctx;
    }

    def propertyMissing(String name) {
        ctx.read('$.' + name)
    }
}

class BDD {
    def stat = [req: 0, assert: 0]

    def g =
            [debug             : true,
             server            : null,
             query             : [:],
             body              : null,
             headers           : [:],
             requestContentType: URLENC,
             contentType       : TEXT];

    def r = null

    // response
    def resp, respBody

    // expect
    def http, json, xml

    // save
    def cookie = [:]

    // temp object
    def obj = [:]

    def reset() {
        r = g.clone();
        http = [:]
        json = [:]
        xml = [:]
    }

    def config(Map config) {
        config.each { key, value ->
            g[key] = value
        }
    }

    def http(method, url, config) {
        reset()

        if (config) {
            config.delegate = this
            config.call()
        }

        def http = new HTTPBuilder(r.server)
        http.ignoreSSLIssues()
        http.headers = [*: g.headers, *: r.headers, "Accept" : "*/*"]

        if (!http.headers.cookie) {
            cookie.each { key, value ->
                http.headers["cookie"] = key + "=" + value
            }
        }

        URIBuilder reqUri = new URIBuilder(url)
        reqUri.query = r.query

        if (r.body instanceof byte[]) {
            r.requestContentType = BINARY
        } else if (r.body && reqUri.query) {
            reqUri.query << r.body
        }

        stat.req++;
        if (r.debug) {
            println "DEBUG REQUEST: ${reqUri}"
            println "DEBUG REQUEST COOKIE: ${http.headers}"
        }

        http.request(method) { req ->
            uri.path = url
            uri.query = r.query
            contentType = r.contentType

            if (method == POST || method == PUT) {
                body = r.body
                requestContentType = r.requestContentType
            }

            response.success = { resp, body ->
                this.resp = resp
                this.respBody = body.getText()
            }

            response.failure = { resp ->
                this.resp = resp
            }
        }
    }

    def expect(config) {
        config.delegate = this
        config.call()

        stat.assert++
        if (http.code) assert http.code == resp.status
        else assert 200 == resp.status

        if (json) {
            jsonExpect()
        } else if (xml) {
            xmlExpect()
        }
    }


    def jsonExpect() {
        if (r.debug) println("DEBUG RESPONSE: ${respBody}")

        def ctx = JsonPath.parse(respBody)
        assert null != ctx

        json.each { key, value ->
            stat.assert++;

            if (value == NotNull) {
                assert null != ctx.read('$.' + key)
            } else if (value == IsString) {
                assert ctx.read('$.' + key) instanceof String
            } else if (value == IsInteger) {
                def actual = ctx.read('$.' + key)
                assert actual instanceof Integer || actual instanceof Long
            } else if (value == NotEmpty) {
                assert 0 != ctx.read('$.' + key + ".length()")
            } else if (value instanceof Grep) {
                def closure = value.closure
                this.obj = [:]
                closure.delegate = this
                closure.call()

                assert ctx.read('$.' + key).find {
                    obj.every { k, v ->
                        it[k] == v
                    }
                }

            } else if (key == 'closure') {
                value.call(new JsonReader(ctx))
            } else {
                assert value == ctx.read('$.' + key)
            }
        }
    }

    def xmlExpect() {
        if (r.debug) println("DEBUG RESPONSE: ${respBody}")

        def parser = new XmlSlurper(new Parser())
        def docs = parser.parseText(respBody)
        assert docs != null

        xml.each { key, value ->
            stat.assert++;

            if (value == NotNull) {
                assert null != walkGPath(docs, key)
            } else if (value == NotEmpty) {
                assert 0 != walkGPath(docs, key).length
            } else if (key == 'closure') {
                value.call(docs)
            } else {
                assert value == walkGPath(docs, key)
            }
        }
    }

    def walkGPath(def root, def key){
        def keys = key.split("\\.")
        keys.each {
            root = root."$it"
        }
        return root.text()
    }

    def save(config) {
        config.delegate = this
        config.call()
    }

    static NEW() {
        return new BDD()
    }

    static CONFIG(BDD bdd, option) {
        bdd.config(option)
    }

    static GET(BDD bdd, url, config = null) {
        bdd.http(GET, url, config)
    }

    static POST(BDD bdd, url, config = null) {
        bdd.http(POST, url, config)
    }

    static PUT(BDD bdd, url, config = null) {
        bdd.http(PUT, url, config)
    }

    static DELETE(BDD bdd, url, config = null) {
        bdd.http(DELETE, url, config)
    }

    static EXPECT(BDD bdd, config) {
        bdd.expect(config)
    }

    static SAVE(BDD bdd, config) {
        bdd.save(config)
    }

    static byte[] F(file) {
        return new File(".", file).bytes;
    }

    static STAT(BDD bdd) {
        println "req   : ${bdd.stat.req}"
        println "assert: ${bdd.stat.assert}"
    }

    static NotNull = { 1 }
    static IsString = { 2 }
    static IsInteger = { 3 }
    static NotEmpty = { 10 }

    static class Grep {
        def closure

        Grep(closure) {
            this.closure = closure
        }
    }

    static Grep(closure) {
        return new Grep(closure)
    }
}


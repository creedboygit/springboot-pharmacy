package com.valletta.pharmacy.pharmacy.service

import com.valletta.pharmacy.direction.service.Base62Service
import spock.lang.Specification


class Base62ServiceTest extends Specification {

    private Base62Service base62Service

    def setup() {
        base62Service = new Base62Service()
    }

    def "check base62 encoder/decoder"() {

        given:
        long num = 19

        when:
        def encodedId = base62Service.encodeDirectionId(num)
        def decodedId = base62Service.decodeDirectionId(encodedId)

        println encodedId
        println decodedId

        then:
        num == decodedId
    }
}
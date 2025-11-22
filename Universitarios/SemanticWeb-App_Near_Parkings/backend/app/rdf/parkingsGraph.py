from fastapi import requests
from rdflib import Graph, Namespace, Literal, XSD
from rdflib.plugins.sparql import prepareQuery

DATA_FILE = "https://raw.githubusercontent.com/SergioZSZ/Proyectos/refs/heads/main/Universitarios/SemanticWeb-App_Near_Parkings/backend/app/rdf/parkings-with-links.ttl"

SCHEMA = Namespace("http://schema.org/")
MY = Namespace("https://data.smartcitymadrid.es/ontology/parking#")
DCT = Namespace("http://purl.org/dc/terms/")
GEO = Namespace("http://www.opengis.net/ont/geosparql#")
EX = Namespace("http://group20.linkeddata.es/parkings/resource/")
OWL    = Namespace("http://www.w3.org/2002/07/owl#")
VCARD  = Namespace("http://www.w3.org/2006/vcard/ns#")
g = Graph()
g.parse(DATA_FILE, format="turtle")
########################################################################################3
q_buscar_parking_nearby = prepareQuery(
      '''
    SELECT  ?id ?long ?lat 
    WHERE {
      ?p a schema:ParkingFacility ;
         dct:identifier ?id ;
         OPTIONAL {
    ?p geo:hasGeometry ?geom .
        OPTIONAL { ?geom owl:sameAs ?openStreetMapCoordinates . }
        OPTIONAL { ?geom geo:lat ?lat . }
        OPTIONAL { ?geom geo:long ?long . }
        OPTIONAL { ?geom geo:asWKT ?wkt . }
        }
    }
    ''',
    initNs={"schema": SCHEMA, "dct": DCT,"geo":GEO}
)



########################################################################################3
q_buscar_parking_id = prepareQuery(
      '''
    SELECT  ?id 
    WHERE {
      ?p a schema:ParkingFacility ;
         dct:identifier ?id ;
    }
    ''',
    initNs={"schema": SCHEMA, "dct": DCT}
)
    
########################################################################################3
q_buscar_parking = prepareQuery(
        '''
        SELECT 
            ?id ?family ?name ?email ?URLIcon ?streetAddress ?postalCode ?nautonomousCommunity
            ?wikidataAutonomousCommunity ?category ?categoryCode ?ncountry ?wikidataCountry
            ?lat ?long ?openStreetMapCoordinates ?ncity ?wikidataCity ?EMTParking ?type
        WHERE {
    ?p a schema:ParkingFacility ;
    dct:identifier ?id .

    OPTIONAL { ?p my:hasFamily ?family. }
    OPTIONAL { ?p schema:name ?name. }
    OPTIONAL { ?p vcard:email ?email. }          
    OPTIONAL { ?p my:isEMTParking ?EMTParking. }
    OPTIONAL { ?p my:hasCategory ?category. }
    OPTIONAL { ?p my:hasCategoryCode ?categoryCode. }
    OPTIONAL { ?p my:hasType ?type. }
    OPTIONAL { ?p my:hasURLIcon ?URLIcon. }

    OPTIONAL {
    ?p my:containedInCity ?city .
        OPTIONAL { ?city schema:name ?ncity}
        OPTIONAL { ?city owl:sameAs ?wikidataCity. }
        }

    OPTIONAL { ?city my:containedInRegion ?autonomousCommunity . }
        OPTIONAL { ?autonomousCommunity owl:sameAs ?wikidataAutonomousCommunity . }
        OPTIONAL { ?autonomousCommunity schema:name ?nautonomousCommunity .}

    OPTIONAL { ?city my:containedInNation ?country . }
        OPTIONAL { ?country schema:name ?ncountry .}
        OPTIONAL { ?country owl:sameAs ?wikidataCountry . }

    OPTIONAL {
    ?p geo:hasGeometry ?geom .
        OPTIONAL { ?geom owl:sameAs ?openStreetMapCoordinates . }
        OPTIONAL { ?geom geo:lat ?lat . }
        OPTIONAL { ?geom geo:long ?long . }
        OPTIONAL { ?geom geo:asWKT ?wkt . }
        }

    OPTIONAL {
    ?p schema:address ?addr .
        OPTIONAL { ?addr schema:streetAddress ?streetAddress . }
        OPTIONAL { ?addr schema:postalCode ?postalCode . }
        OPTIONAL { ?addr schema:addressRegion ?addrRegion . }   # si quieres el nombre literal
        OPTIONAL { ?addr schema:addressCountry ?addrCountry . } # idem
        }
    }
        ''',
        initNs={
            "schema": SCHEMA,
            "my": MY,
            "geo": GEO,
            "dct": DCT,
            "owl": OWL,
            "vcard":VCARD
        }
    )


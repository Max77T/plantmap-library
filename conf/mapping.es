{
"map": {
  "properties": {
    "path": {
      "type": "string"
    },
    "generationDate": {
      "type": "date",
      "format": "yyyy-MM-dd"
    },
    "dpi": {
      "type": "long"
    },
    "extension": {
      "type": "string"
    },
    "dimension": {
      "type": "string"
    },
    "weight": {
      "type": "long"
    },
    "bBox": {
      "type": "string"
    },
    "projection": {
      "type": "string"
    },
    "taxon": {
      "properties": {
        "cdref": {
          "type": "string"
        },
        "name": {
          "type": "multi_field",
			"fields": {
				"analyzed": {"type": "string"},
				"original": {"type": "string", "index": "not_analyzed"}
			}
        }
      }
    },
    "organizationCbn": {
		"type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "email": {
      "type": "string"
    },
    "projectDescription": {
      "type": "string"
    },
    "projectName": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "rangeObservationStart": {
      "type": "date",
      "format": "yyyy-MM-dd"
    },
    "rangeObservationEnd": {
      "type": "date",
      "format": "yyyy-MM-dd"
    },
    "projectModificationDate": {
      "type": "date",
      "format": "yyyy-MM-dd"
    },
    "versionNumber": {
      "type": "string"
    },
    "genealogyData": {
      "type": "string"
    },
    "thesaurusISO": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "thesaurusINSPIRE": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "thesaurusCBN": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "keywords": {
      "type": "string"
    },
    "dataStatus": {
      "type": "string"
    },
    "usageLimit": {
      "type": "string"
    },
    "updateFrequency": {
      "type": "string"
    },
    "dataOwner": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "contactName": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "contactInspire": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "cbnManager": {
      "type": "multi_field",
		"fields": {
			"analyzed": {"type": "string"},
			"original": {"type": "string", "index": "not_analyzed"}
		}
    },
    "isPrivate": {
      "type": "boolean"
    }
  }
}
}

// Format the dates selected through calendar widgets
function formatDate(opts){
	if (opts.value()){
		var year = opts.value().getFullYear().toString();
		var month = (opts.value().getUTCMonth() + 1) % 12;
		
		if(opts.value().getDate() == 1){
			month = (opts.value().getUTCMonth() + 2) % 12;
		}
		
		if(month < 10){
			month = "0" + month;
		}

		var day = opts.value().getDate();
		if(day < 10){
			day = "0" + day;
		}

		return year + "-" + month + "-" + day;
	}
}

/**
 * Contains the data for one thumbnail.
 */
function etiquette(data) {
	var self = this;
	self.img = data.img;
	self.id = data.id;
	self.projectName = data.projectName;
	self.taxonName = data.taxonName;
	self.cdref = data.cdref;
	self.isSelected = ko.observable(false);
	self.isPrivate = ko.observable(data.isPrivate);
	self.organizationCbn = data.organizationCbn;
	self.projectDescription = data.projectDescription;
	
	self.tooltipText = ko.pureComputed(function() {
		return "<strong>" + translation.searchTooltipProject + ":</strong> " + self.projectName + "<br />" 
		+ "<strong>" + translation.searchTooltipOrganism + ":</strong> " + self.organizationCbn + "<br />" 
		+ "<strong>" + translation.searchTooltipDescription + ":</strong> " + self.projectDescription + "<br />"
		+ "<strong>" + translation.searchTooltipTaxon + ":</strong> " + self.cdref + ' - ' + self.taxonName;
	});
	
	self.projectShortName = ko.pureComputed(function(){
		return cut(self.projectName, 25);
	});
	
	self.taxonShortName = ko.pureComputed(function(){
		return cut(self.taxonName, 30);
	});
}

/**
 * Cuts the string if string.length > size. Replace last three characters by '...'.
 */
function cut(string, size){
	if(string.length <= size){
		return string;
	}
	return string.slice(0, size - 3) + "...";
}

/**
 * Main view model for search page.
 * Handle state and dynamism of the search page.
 * See http://knockoutjs.com/ for more information.
 */
function etiquettesViewModel() {
	var self = this;
	// Contains search results
	self.etiquettes = ko.observableArray([]);
	
	// Action of the button selectAll
	self.selectAll = ko.computed({
		read: function() {
			if(self.etiquettes() <= 0){
				return false;
			}
			
            var firstUnchecked = ko.utils.arrayFirst(self.etiquettes(), function(item) {
                return item.isSelected() == false;
            });
            return firstUnchecked == null;
        },
        write: function(value) {
            ko.utils.arrayForEach(self.etiquettes(), function(item) {
                item.isSelected(value);
            });
        }
    }).extend({ throttle: 1 });
	
	// Search stats
	self.timeTaken = ko.observable(0);
	self.displayTimeTaken = ko.observable(false);
	self.searchTime = ko.pureComputed(function() {
		return (self.timeTaken() / 1000) + "s" ;
	});

	// Calendars
	self.opts = {
		value: ko.observable(""),
		showCalendar: true,
		showToday: true,
		showTime: false,
		showNow: true,
		militaryTime: true
	};
	
	self.optsEnd={
		value: ko.observable(""),
		showCalendar: true,
		showToday: true,
		showTime: false,
		showNow: true,
		militaryTime: true
	};

	self.rangeGenerationDateEnd = ko.pureComputed(function () {
		return formatDate(self.optsEnd);
	});
	
	self.rangeGenerationDateStart = ko.pureComputed(function () {
		return formatDate(self.opts);
	});

	// Search parameters
	self.searchTerms = ko.observable(); // Global query
	self.keywords = ko.observable();
	self.projectName = ko.observable();
	self.contactName = ko.observable();
	self.visibility = ko.observable("");
	self.organizationCbn = ko.observable("");
	self.taxon = ko.observable();
	
	// Pagination
	self.totalResultCount = ko.observable(0);
	self.pageNumber = ko.observable(1);
	self.pageSize = ko.observable();
	self.pageCount = ko.pureComputed(function() {
		return Math.ceil(self.totalResultCount() / self.pageSize());
	});

	// Page numbers we want to provide "links" to
	self.pageLinks = ko.pureComputed(function() {
		var endDelta = self.pageCount() - self.pageNumber();
		var endDeltaToApply = endDelta > 3 ? 0 : 3 - endDelta;
		var startId = Math.max(self.pageNumber() - (3 + endDeltaToApply), 1);
		var endId = Math.min(startId + 6, self.pageCount());

		var pages = [];
		for(var i = startId; i <= endId; i++){
			pages.push(i);
		}
		return pages;
	});

	// Reset search results
	self.clearViewModel = function(){
		self.etiquettes.removeAll();
		self.selectAll(false);
		self.pageNumber(1);
		self.totalResultCount(0);
		self.timeTaken(0);
		self.displayTimeTaken(false);
	}

	// Pagination actions
	self.previousPage = function(){
		if(self.pageNumber() <= 1)
			return;

		self.pageNumber(self.pageNumber() - 1);
		self.search();
	}

	self.nextPage = function(){
		if(self.pageNumber() >= self.pageCount())
			return;

		self.pageNumber(self.pageNumber() + 1);
		self.search();
	}

	self.loadPage = function(number){
		if(self.pageNumber() < 1 || self.pageNumber() > self.pageCount())
			return;

		self.pageNumber(number);
		self.search();
		console.log("loadPage " + number)
	}

	// Get form field values
	self.getFormData = function(){
		return {
			searchTerms: self.searchTerms(),
			keywords: self.keywords(),
			projectName: self.projectName(),
			rangeGenerationDateStart: self.rangeGenerationDateStart(),
			rangeGenerationDateEnd: self.rangeGenerationDateEnd(),
			contactName: self.contactName(),
			visibility: self.visibility(),
			organizationCbn: self.organizationCbn(),
			taxon: self.taxon(),
			pageNumber: self.pageNumber(),
			pageSize: self.pageSize(),
		};
	};

	// Populate viewModel with data
	self.setResults = function(data){
		self.etiquettes.removeAll();
		for(var i = 0; i < data.results.length; i++){
			self.etiquettes.push(
					new etiquette({
						projectName: data.results[i].projectName,
						img : data.results[i].url,
						id: data.results[i].id,
						cdref: data.results[i].taxon.cdref,
						isPrivate: data.results[i].isPrivate,
						organizationCbn: data.results[i].organizationCbn,
						projectDescription: data.results[i].projectDescription,
						taxonName: data.results[i].taxon.name
						})
			);
		}
		self.totalResultCount(data.nbHits);
		self.timeTaken(data.timeTaken);
	}

	// Perform a search with value in form fields
	self.search = function() {
		var data = self.getFormData();
		self.etiquettes.removeAll();

		$("#js-etiquettes-spinner").show();
		$("#js-search-ajax-error-block").hide();

		$.ajax(config.searchUrl, {
			data: ko.toJSON(data),
			type: "post", contentType: "application/json",
			datatype: "json",
			success: function(data){
				self.setResults(data);
				self.displayTimeTaken(true);
			},
			error: function(){
				$("#js-search-ajax-error-block").show();
			},
			complete: function(){
				$("#js-etiquettes-spinner").hide();
			}
		});
	};
	
	/**
	 * Download selected maps from server.
	 * It's done in two request:
	 * - First ask the server to build an archive containing 
	 * 	 the selected maps and return its id
	 * - Then ask the server to send the archive
	 */
	self.downloadSelected = function() {
		var data = self.getIdList();
		if(data.idList.length <= 0){
			return;
		}
		
		// First step
		$.ajax(config.downloadUrl, {
			data: ko.toJSON(data),
			type: "post", contentType: "application/json",
			datatype: "json",
			success: function(data){
				// Second step
				window.location.href = data.fileUrl; 
			},
			error: function(){
				$("#js-search-ajax-error-block").show();
			}
		});
	};
	
	// Get all the selected results
	self.getIdList = function(){
		var selectedEtiqs = ko.utils.arrayFilter(this.etiquettes(), function(etiq) {
            return etiq.isSelected();
        });
		
		var ids = ko.utils.arrayMap(selectedEtiqs, function(etiq) {
	        return etiq.id;
	    });	
		
		return {
			idList: ids
		};
	};

	// Delete selected maps
	self.deleteSelected = function() {
		var data = self.getIdList();
		if(data.idList.length <= 0){
			return;
		}
		
		var confirm_delete = confirm(translation.searchDeleteConfirmation);
	    if (!confirm_delete) {
	        console.log("delete canceled");
	        return;
	    }
	    
	    var allSelected = self.selectAll();
	    var pageNumber = self.pageNumber();
	    console.log("delete " + data.idList);
		$.ajax(config.deleteUrl, {
			data: ko.toJSON(data),
			type: "post", contentType: "application/json",
			datatype: "json",
			success: function(){
				if(allSelected && pageNumber > 1){
					self.pageNumber(pageNumber - 1);
				}
				self.search();
			},
			error: function(){
				$("#js-search-ajax-error-block").show();
			}
		});
	};

	// Reset pageNumber when user change values in text fields
	self.formFieldsDirtyChecking = ko.computed(function() {
		// Used to subscribes to fields modification
		self.searchTerms();
		self.keywords();
		self.projectName();
		self.contactName();
		self.taxon();
		self.rangeGenerationDateStart();
		self.rangeGenerationDateEnd();

		self.clearViewModel();
		return "";
	});
	
	// Launch a new search when select value change
	self.cleanThenSearch = function() {
		self.clearViewModel();
		self.search();
	}
	
	// Reload page when page size change
	self.pageSize.subscribe(function(newValue) {
		self.cleanThenSearch();
	});
}

function initSearchPage(){
	// Custom binding for selectAll button
	ko.bindingHandlers.checkbox = {
	    init: function(element, valueAccessor, allBindings, data, context) {
	        var $element, observable;
	        observable = valueAccessor();
	        if (!ko.isWriteableObservable(observable)) {
	            throw "You must pass an observable or writeable computed";
	        }
	        $element = $(element);
	        $element.on("click", function() {
	            observable(!observable());
	        });
	        ko.computed({
	            disposeWhenNodeIsRemoved: element,
	            read: function() {
	                $element.toggleClass("active", observable());
	            }
	        });
	    }
	};
	
	var etiquetteVM = new etiquettesViewModel();
	
	// Launch a search when enter button is pressed in form
	$(".js-filter-search-on-enter").keypress(function(e) {
	    if(e.which == 13) {
	    	etiquetteVM.search();
	    }
	});
	
	// Activate bootstrap tooltips
	$("body").tooltip({ 
		selector: '[data-toggle=tooltip]' 
	});

	// Launch knockout
	ko.applyBindings(etiquetteVM);
}
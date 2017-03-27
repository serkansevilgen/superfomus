Fomus {
	var <eventList;
	var <>fileName = "~/Desktop/SCFomus_";
	var <>lilyPath;
	var <>lilyViewPath;
	var <>timeTag;
	var <>qt = true;

	*new { arg noteList, n;
		^super.new.init(noteList, n);
	}

	init { arg that=[], n=1;

		Platform.case(
			\osx, {
				lilyPath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond ";
				lilyViewPath = "open ";
				"Please check LilyPond and PDF viewer paths.".warn
			},
			\linux, {
				lilyPath = "lilypond ";
				lilyViewPath = "xpdf ";
				"Please check LilyPond and PDF viewer paths.".warn
			},
			\windows, {
				"Please set LilyPond and PDF viewer paths.".warn
			}
		);

		eventList = Array.new;
		this.newTag;

		(that.size > 0 or: that.class == Routine).if({
			this.put(that, n)
		});

	}

	put { arg that, n=1;

		case
		{that.class == Event}
		{ eventList = eventList.add(that)}

		{that.class == Array}
		{
			that.do { |thisEvent|
				if( thisEvent.class == Event,
					{ eventList = eventList.add(thisEvent)},
					{ "At least one element of the Array is not an Event".error}
				);
			}
		}

		{that.class == Routine}
		{
			that.nextN(n,()).do { |thisEvent|
				eventList = eventList.add(thisEvent)
			};
		}

		{(that.class == Event or: that.class == Array).not}
		{ "You must provide an Event, a Stream or an Array of Events".error};

	}

	asString {
		var out = String.new;
		eventList.do { arg i;
			out = out ++ i.asFomusString ++ "\n"
		};
		^out
	}

	qtString {
		^this.qt.if(
			{"quartertones = yes"},
			{"quartertones = no"})
	}

	header {
		^(
			this.qtString ++ "\n"
			++ "lily-exe-path = " ++ this.lilyPath ++ "\n"
			++ "lily-view-exe-path = " ++ this.lilyViewPath  ++ "\n"
			++ "part <id apart, inst piano>"  ++ "\n"
			++ "part apart"  ++ "\n" ++ "voice (1 2 3)"  ++ "\n"
		).asString
	}

	newTag { timeTag = Date.getDate.stamp.asString }

	write {
		var file = File(this.fileName.standardizePath ++ timeTag ++ ".fms","w");
		file.write(this.header ++ "\n");
		file.write(this.asString);
		file.close
	}

	ly {
		this.newTag; this.write;
		(
			"fomus " ++ this.fileName.standardizePath ++ timeTag ++ ".fms"
			++ " -o " ++ this.fileName.standardizePath ++ timeTag ++ ".ly"
		).runInTerminal
	}

	midi {
		this.newTag; this.write;
		(
			"fomus " ++ this.fileName.standardizePath ++ timeTag ++ ".fms"
			++ " -o " ++ this.fileName.standardizePath ++ timeTag ++ ".mid"
		).systemCmd
	}

	xml {
		this.newTag; this.write;
		(
			"fomus " ++ this.fileName.standardizePath ++ ".fms" ++
			" -o " ++ this.fileName.standardizePath ++ ".xml"
		).systemCmd
	}

	show { (this.lilyViewPath ++ " " ++ this.fileName.standardizePath ++ ".pdf").unixCmd }

}

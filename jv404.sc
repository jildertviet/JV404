JV404 {
  var <> samples;
  var <> dir;
  var <>recordBuffer;
	*new{
		|path|
		^super.new.init(path);
	}
  init{
    |path|
    var folder = PathName.new(path);
    recordBuffer = Buffer.alloc(Server.local, Server.local.sampleRate * 10.0, 1);
    dir = folder;
    this.loadSynthDef();
    this.registerCustomEvent();
    samples = (0!16)!6;
		if(folder.isFolder, {
      if(folder.entries.size == 0, {
        ("No files in folder " ++ path).error;
      });
			folder.entries.do{
				|e|
				// var bTemp = Buffer.read(Server.local, e.fullPath);
        var sampleIndex = -1;
        var bankLetter;
        var bankIndex = -1;
        sampleIndex = PathName(e.fileNameWithoutExtension).endNumber;
        bankLetter = e.fileName[0];
        bankIndex = bankLetter.ascii - 65; // A.ascii is 65
        if((bankIndex >= 0).and(sampleIndex >= 0), {
          var buffer = Buffer.read(Server.local, e.fullPath);
          var sampleObj = JV404Sample.new(buffer, this);
          samples[bankIndex][sampleIndex] = sampleObj;
        }, {
          "Error with bankIndex or sampleIndex".error;
          [bankIndex, sampleIndex].postln;
        });
			}
		}, {
      ("Path " ++ path ++ " is not a folder").error;
    });
  }
  loadSynthDef{
    SynthDef(\jv404, {
      var player, env;
      var buffer = \buf.ir(0);
      var startPos = \startPos.kr(0);
      var endPos = \endPos.kr(1);
      var loop = \loop.kr(0);
      // var phasor = Phasor.ar(0, BufRateScale.kr(buffer) * \rate.ar(1), startPos * BufFrames.kr(buffer), endPos * BufFrames.kr(buffer), startPos * BufFrames.kr(buffer));
      var phasor = Sweep.ar(1, BufSampleRate.ir(buffer) * \rate.kr(1)) + (BufFrames.ir(buffer) * startPos);
      var envGate = \gate.kr(1);
      var envGate2 = EnvGate.new(1, Trig.ar(1, BufDur.kr(buffer) * (1.0 - (1.0 - endPos) - startPos)), fadeTime: 1e-10); // When loop is on, don't trigger this env
      player = BufRd.ar(1, buffer, phase: phasor, loop: \loop.kr(0)); // Loop means wrap
      env = Env.adsr(\a.kr(1e-8), \d.kr(0.1), \s.kr(1.0), \r.kr(0.1), curve: -4).ar(gate: envGate, doneAction: 2);
      player = Pan2.ar(player, \pan.kr(0));
      Out.ar(\out.kr(0), player * \amp.kr(0.4));
    }).add;
    SynthDef(\jv404Recorder, {
      RecordBuf.ar(SoundIn.ar(\in.kr(0)), \buffer.kr(0), loop: 0, doneAction: 2);
    }).add;
  }
  registerCustomEvent{
    Event.addEventType(\jv404, {
        |server, sampler, amp = 0.4, sustain = 0.3|
        var s = ~sampler.samples[~bank][~id];
        // ~startPos 
        ~buf = s.b;
        ~startPos = ~startPosAdd + s.startPos;
        ~endPos = ~endPosAdd + s.endPos;

        ~type = \note;
        currentEnvironment.play;

        // Create a Synth with custom parameters
        // synth = Synth(\default, [
        //     \freq, freq,
        //     \amp, amp,
        //     \sustain, sustain
        // ]);
        // ^synth;
        // ^Event.default;
    }, parentEvent: (
      startPosAdd: 0,
      endPosAdd: 0,
      bank: 0
    ));
  }
}

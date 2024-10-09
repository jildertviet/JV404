j = JV404.new("/home/jildert/Music/404bank0");
s.boot
j.samples[0][1].b.play
Synth(\jv404, [buf: j.samples[0][0].b]);
j.samples
Env.adsr()
PathName(PathName("test10.wav").fileNameWithoutExtension).endNumber
PathName("test1.wav").fileName[0]
'A'.ascii - 65
'A' - 65
PathName
PlayBuf.ar()
BufRd.ar()
Phasor.ar(gt)
BufRateScale
({
var p = Phasor.ar(1, 1/SampleRate.ir, 0, 1, 0).scope;
var reset = p < Delay1.ar(p);
FreeSelf.kr(Trig.ar(reset));
SinOsc.ar(110!2) * 0.1 * Env.perc().ar(gate: reset);
}.play)
)
)
Buffer
lot
(
    SynthDef(\jv404, {
    // {
      var player, env;
      var buffer = \buf.ir(0);
      var startPos = \start.kr(0);
      var phasor = Phasor.ar(0, BufRateScale.kr(buffer) * \rate.ar(1), startPos, \end.kr(1) * BufFrames.kr(buffer), startPos);
      var envGate = \gate.kr(1);
      player = BufRd.ar(1, buffer, phase: phasor, loop: \loop.kr(1));
      env = Env.adsr(\a.kr(1e-8), \d.kr(0.1), \s.kr(1.0), \r.kr(0.1), curve: -4).ar(gate: envGate, doneAction: 2);
      player = Pan2.ar(player, \pan.kr(0));
      Out.ar(\out.kr(0), player * \amp.kr(0.4));
    }).add;
  )

Synth(\jv404, [buf: j.samples[0][0], amp: 1, endPos: 0.1]);
(
    SynthDef(\jv404, {
      var player, env;
      var buffer = \buf.ir(0);
      var startPos = \startPos.kr(0);
      var endPos = \endPos.kr(1);
      var phasor = Phasor.ar(0, BufRateScale.kr(buffer) * \rate.ar(1), startPos * BufFrames.kr(buffer), endPos * BufFrames.kr(buffer), startPos * BufFrames.kr(buffer));
      var envGate = \gate.kr(1);
      var envGate2 = EnvGate.new(1, Trig.ar(1, BufDur.kr(buffer) * (1.0 - (1.0 - endPos) - startPos)), fadeTime: 1e-10); // When loop is on, don't trigger this env
      // FreeSelf.kr(HPZ2.ar(phasor < Delay1.ar(phasor)));
      player = BufRd.ar(1, buffer, phase: phasor, loop: \loop.kr(1)); // Loop means wrap
      env = Env.adsr(\a.kr(1e-8), \d.kr(0.1), \s.kr(1.0), \r.kr(0.1), curve: -4).ar(gate: envGate, doneAction: 2);
      player = Pan2.ar(player, \pan.kr(0));
      Out.ar(\out.kr(0), player * \amp.kr(0.4));
    }).add;

)

(
Pdef(\testje,
Pbind(\instrument, \jv404,
  \type, \jv404,
  \sampler, j,
  [\dur, \delta], Pseqs("■□□□■□□□■□□□■▪□□", inf) / 16,
  // \dur, 0.1,
  // \delta, 0.25,
  \rate,Pseq([1, 1, 1, 1], inf), 
  \loop, 0,
  \amp, Pseq([1, 1, 0.3, 0.5], inf),
  \a, 0.04,
  \r, Pseq([0.1, 0.2, 0.3], inf),
  \s, 0.5,
  \curve, -2,
  \d, 0.3,
  \id, Pseq([9, 1, 2, 0], inf),
  // \buf, 1,
  // \endPosAdd, 0,
  // \out, [0,~reverb],
  \startPosAdd,Pseq([0.0, 0, 0, 0.0], inf),
)).play;
)
~reverbSynth
Rect
x = Plotter.new("X");
x = j.samples[0][0].b.plot
x.inspect
Event
Buffer
RecordBuf
Synth
Sweep

j.samples[0][9].gui
j.samples[0][0].b.numFrames
j.recordBuffer.play
j.samples[0][0].b.plot
j.samples[0][0].b.play
j.samples[0][0].b.numFrames
x = Synth(\jv404, [buf: j.samples[0][0].b, loop: 1, doneAction: 1]);
x.set(\replay, 1)
x.run
x.free
0.123.asInteger
(
    SynthDef(\jv4043, {
      var player, env;
      var buffer = \buf.ir(0);
      var startPos = \startPos.kr(0);
      var doneAction = \doneAction.kr(2);
      var endPos = \endPos.kr(1);
      var loop = \loop.kr(0);
      // var phasor = Phasor.ar(0, BufRateScale.kr(buffer) * \rate.ar(1), startPos * BufFrames.kr(buffer), endPos * BufFrames.kr(buffer), startPos * BufFrames.kr(buffer));
      var envGate = \gate.kr(1);
      var phasor = Sweep.ar(\replay.tr(1), BufSampleRate.ir(buffer) * \rate.kr(1)) + (BufFrames.ir(buffer) * startPos);
      var envGate2 = EnvGate.new(1, Trig.ar(1-loop, BufDur.kr(buffer) * (1.0 - (1.0 - endPos) - startPos)), fadeTime: 1e-10, doneAction: doneAction); // When loop is on, don't trigger this env
      player = BufRd.ar(1, buffer, phase: phasor, loop: 1); // Loop means wrap
      env = Env.adsr(\a.kr(1e-8), \d.kr(0.1), \s.kr(1.0), \r.kr(0.1), curve: -4).ar(gate: envGate, doneAction: doneAction);
      player = Pan2.ar(player, \pan.kr(0));
      Out.ar(\out.kr(0), player * \amp.kr(0.4));
    }).add; 
)
Synth(\jv404, [buf: j.samples[0][0].b, amp: 1, loop: 0]);
j.samples[0][0].b.play

j.recordBuffer.copyData(j.samples[0][0].b, numSamples: j.samples[0][0].b.numFrames);
j.recordBuffer.plot
Buffer.alloc()
SampleRate.ir.post
s.sampleRate
s.inspect

Recorder
b = Buffer.alloc(s, s.sampleRate * 4, 1);
b.free
b.numFrames
OSCdef
c.plot
b.copyData(c, numSamples: 10000);
c.plot
(
SynthDef(\jv404Recorder, {
      RecordBuf.ar(SoundIn.ar(\in.kr(0)), b, loop: 0, doneAction: 2);
    }).play;
)
b.plot
{SoundIn.ar(0)}.scope
j.saveSettings
j.samples[0][0].render
j.loadSettings
String
"a\nb\nc\n".split($\n)

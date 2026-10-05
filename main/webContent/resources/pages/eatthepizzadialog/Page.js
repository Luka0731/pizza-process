/**
 * The eating game on the "eat the pizza" page.
 * - spreads all pizzas randomly over the table
 * - a click eats a pizza: crunch sound, the pizza shrinks away
 * - when the last pizza is gone it calls eatThePizzaDone(), the p:remoteCommand that closes the dialog
 */
PizzaProcess.register('eatPage', {
  init: function (roots) {
    'use strict';

    roots.forEach(function (root) {
      root.querySelectorAll('.eat-page__table').forEach(function (table) {
        if (table.dataset.ready) return;
        table.dataset.ready = 'true';
        setUp(table);
      });
    });



    // |----- game -----|

    function setUp(table) {
      var pizzas = Array.prototype.slice.call(table.querySelectorAll('.eat-page__pizza'));
      var left = pizzas.length;

      if (left === 0) {
        finish();
        return;
      }

      pizzas.forEach(function (pizza) {
        place(pizza, table);
        pizza.addEventListener('click', function () {
          if (pizza.classList.contains('eat-page__pizza--eaten')) return;
          pizza.classList.add('eat-page__pizza--eaten');
          playEatingSound();

          left--;
          if (left === 0) setTimeout(finish, 700); // lets the last pizza disappear first
        });
      });
    }

    function place(pizza, table) {
      var size = Math.min(table.clientWidth, table.clientHeight) * random(0.22, 0.34);
      pizza.style.width = size + 'px';
      pizza.style.height = size + 'px';
      pizza.style.left = random(0, table.clientWidth - size) + 'px';
      pizza.style.top = random(0, table.clientHeight - size) + 'px';
      pizza.style.transform = 'rotate(' + random(-30, 30) + 'deg)';
      pizza.style.zIndex = String(Math.round(random(1, 20)));
    }

    function finish() {
      if (typeof window.eatThePizzaDone === 'function') window.eatThePizzaDone();
    }

    function random(min, max) {
      return min + Math.random() * (max - min);
    }



    // |----- sound -----|

    /**
     * Three short crunches, made with the web audio api so no sound file is needed.
     * A crunch is a very short burst of filtered noise.
     */
    function playEatingSound() {
      var AudioContext = window.AudioContext || window.webkitAudioContext;
      if (!AudioContext) return;

      var app = window.PizzaProcess;
      app.audioContext = app.audioContext || new AudioContext();
      var context = app.audioContext;
      if (context.state === 'suspended') context.resume();

      [0, 0.16, 0.32].forEach(function (delay) {
        crunch(context, context.currentTime + delay);
      });
    }

    function crunch(context, startTime) {
      var duration = 0.09;
      var buffer = context.createBuffer(1, Math.floor(context.sampleRate * duration), context.sampleRate);
      var samples = buffer.getChannelData(0);
      for (var i = 0; i < samples.length; i++) {
        samples[i] = (Math.random() * 2 - 1) * (1 - i / samples.length); // noise that fades out
      }

      var noise = context.createBufferSource();
      noise.buffer = buffer;

      var filter = context.createBiquadFilter();
      filter.type = 'bandpass';
      filter.frequency.value = random(900, 1800);
      filter.Q.value = 0.9;

      var volume = context.createGain();
      volume.gain.value = 0.5;

      noise.connect(filter);
      filter.connect(volume);
      volume.connect(context.destination);
      noise.start(startTime);
    }
  }
});

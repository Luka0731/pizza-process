/**
 * The little game at the end: every ordered pizza lies somewhere on the table, a click eats it.
 * When the last one is gone, the thanks card shows up.
**/
class EatThePizzaDialogPage {
  static #crumbColors = ['--terracotta', '--ochre', '--clay', '--olive', '--bone', '--taupe'];
  static #crumbShapes = ['square', 'bar', 'triangle'];
  static #crumbStyles = ['burst', 'spill', 'spray'];
  static #audio = null;   // { context, buffer } once the sound is decoded

  static init(roots) {
    for (const root of roots) {
      for (const table of root.querySelectorAll('.eat-page__table:not([data-ready])')) {
        table.dataset.ready = 'true';
        EatThePizzaDialogPage.#setUp(table);
      }
    }
  }



  // |----- game -----|

  static #setUp(table) {
    EatThePizzaDialogPage.#loadSound(table.dataset.eatSound);
    for (const pizza of table.querySelectorAll('.eat-page__pizza')) {
      EatThePizzaDialogPage.#place(pizza, table);
      pizza.addEventListener('click', () => EatThePizzaDialogPage.#eat(pizza, table), { once: true });
    }
  }

  static #place(pizza, table) {
    const size = Math.min(table.clientWidth, table.clientHeight) * (0.22 + Math.random() * 0.12);
    Object.assign(pizza.style, {
      width: `${size}px`,
      height: `${size}px`,
      left: `${Math.random() * (table.clientWidth - size)}px`,
      top: `${Math.random() * (table.clientHeight - size)}px`,
      transform: `rotate(${Math.random() * 60 - 30}deg)`,
    });
  }

  static #eat(pizza, table) {
    EatThePizzaDialogPage.#crumbs(pizza, table);
    pizza.classList.add('eat-page__pizza--eaten');
    EatThePizzaDialogPage.#munch(table.dataset.eatSound);

    const pizzasLeft = table.querySelectorAll('.eat-page__pizza:not(.eat-page__pizza--eaten)').length;
    if (pizzasLeft === 0) {
      setTimeout(() => EatThePizzaDialogPage.#showThanks(table), 600);
    }
  }

  static #showThanks(table) {
    table.parentNode.querySelector('.eat-page__thanks').classList.add('eat-page__thanks--visible');
  }



  // |----- effects -----|

  /*
   * the sound gets downloaded and decoded as soon as the table is set up, so even the first bite plays right away.
   * the audio context may only start after a click, that happens in #munch.
   */
  static async #loadSound(sound) {
    const AudioContext = window.AudioContext || window.webkitAudioContext;
    if (!AudioContext || EatThePizzaDialogPage.#audio) return;
    try {
      const context = new AudioContext();
      const response = await fetch(sound);
      const buffer = await context.decodeAudioData(await response.arrayBuffer());
      EatThePizzaDialogPage.#audio = { context, buffer };
    } catch (ex) {
      EatThePizzaDialogPage.#audio = null; // #munch falls back to a normal <audio>
    }
  }

  // every bite sounds a bit different: a slower playback is deeper, a faster one higher
  static #munch(sound) {
    const rate = EatThePizzaDialogPage.#between(0.75, 1.35);
    const loaded = EatThePizzaDialogPage.#audio;

    if (loaded) {
      loaded.context.resume();
      const source = loaded.context.createBufferSource();
      source.buffer = loaded.buffer;
      source.playbackRate.value = rate;
      source.connect(loaded.context.destination);
      source.start();
      return;
    }

    const audio = new Audio(sound);
    audio.preservesPitch = false;
    audio.mozPreservesPitch = false;
    audio.webkitPreservesPitch = false;
    audio.playbackRate = rate;
    audio.play().catch(() => {});
  }

  /*
   * crumbs fly out of the pizza, every bite picks one of three styles:
   * - burst: all around
   * - spill: mostly down, like it falls off the plate
   * - spray: to one side
   * count, size, shape, color, speed and spin are random too.
   */
  static #crumbs(pizza, table) {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches || !Element.prototype.animate) return;

    const between = EatThePizzaDialogPage.#between;
    const pick = (list) => list[Math.floor(Math.random() * list.length)];
    const styles = getComputedStyle(document.documentElement);
    const centerX = pizza.offsetLeft + pizza.offsetWidth / 2;
    const centerY = pizza.offsetTop + pizza.offsetHeight / 2;
    const style = pick(EatThePizzaDialogPage.#crumbStyles);
    const sprayAngle = between(0, Math.PI * 2);
    const count = Math.round(between(8, 18));

    for (let i = 0; i < count; i++) {
      const crumb = document.createElement('span');
      const shape = pick(EatThePizzaDialogPage.#crumbShapes);
      const size = between(3, 10);
      crumb.className = `eat-page__crumb eat-page__crumb--${shape}`;
      Object.assign(crumb.style, {
        left: `${centerX}px`,
        top: `${centerY}px`,
        width: `${shape === 'bar' ? size * 2 : size}px`,
        height: `${size}px`,
        background: styles.getPropertyValue(pick(EatThePizzaDialogPage.#crumbColors)),
      });
      table.appendChild(crumb);

      let angle = between(0, Math.PI * 2);
      if (style === 'spill') angle = between(Math.PI * 0.15, Math.PI * 0.85);       // downwards
      if (style === 'spray') angle = sprayAngle + between(-0.45, 0.45);            // one side
      if (i % 3 === 0) angle = between(Math.PI * 1.15, Math.PI * 1.85);           // every third one always flies up

      const distance = pizza.offsetWidth * between(0.3, style === 'spill' ? 0.6 : 1);
      const x = Math.cos(angle) * distance;
      const y = Math.sin(angle) * distance;
      const fall = between(20, style === 'spill' ? 120 : 60);
      const spin = between(-540, 540);

      crumb
        .animate(
          [
            { transform: 'translate(0, 0) rotate(0deg)', opacity: 1 },
            { transform: `translate(${x}px, ${y}px) rotate(${spin * 0.6}deg)`, opacity: 1, offset: between(0.45, 0.7) },
            { transform: `translate(${x * 1.1}px, ${y + fall}px) rotate(${spin}deg)`, opacity: 0 },
          ],
          // fast out, then slowing down and dropping, like something thrown
          { duration: between(900, 1400), easing: 'cubic-bezier(0.2, 0.6, 0.35, 1)', delay: between(0, 80) },
        )
        .finished.then(() => crumb.remove());
    }
  }



  // |----- helper methods -----|

  static #between(min, max) {
    return min + Math.random() * (max - min);
  }
}





// |----- lifecycle -----|

PizzaProcess.register('EatThePizzaDialogPage', EatThePizzaDialogPage);

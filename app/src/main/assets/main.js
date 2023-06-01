//timeline
const tl = gsap.timeline();

tl.from(".main-container .wrapper1",{
    x:-100,
    duration:5,
    opacity: 0,
    ease: Elastic.easeOut
}).from(".main-container .wrapper2",{
  x:100,
  duration:5,
  opacity: 0,
  ease: Elastic.easeOut
}, "-=5");

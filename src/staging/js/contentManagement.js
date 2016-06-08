$(document).ready(function() {
  $("#title").keypress(function(event) {
    if(event.which == '13') {
      return false;
    }
  });
});

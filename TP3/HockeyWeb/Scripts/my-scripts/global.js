var periodLength = 1200;

function padLeft(number, char, length) {
    var str = "" + number;
    var pad = Array(length + 1).join(char);
    var ans = pad.substring(0, pad.length - str.length) + str;
    return ans;
}

function getPeriod(chrono) {
    var tempChrono = ((chrono % periodLength) === 0 ? chrono - 1 : chrono);
    if (chrono <= 0) return 3;
    return (3 - Math.floor(tempChrono / periodLength));
}

function getTime(chrono) {
    var minutes = 20, seconds = 0;

    if ((chrono % periodLength) !== 0) {
        minutes = Math.floor((chrono % periodLength) / 60);
        seconds = (chrono % periodLength) % 60;
    } else if (chrono <= 0) {
        minutes = 0;
    }

    return padLeft(minutes, "0", 2) + ":" + padLeft(seconds, "0", 2);
}
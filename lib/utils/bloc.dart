import 'dart:async';
import 'dart:developer';
import 'package:flutter/services.dart';
import 'package:rxdart/rxdart.dart';

abstract class Bloc {
  void dispose();
}

class DeepLinkBloc extends Bloc {
  static const stream = const EventChannel('nixsum/events');
  static const platform = const MethodChannel('nixsum/channel');

  final _stateController = BehaviorSubject<String>();

  Stream<String> get state => _stateController.stream;

  Sink<String> get stateSink => _stateController.sink;

  DeepLinkBloc() {
    startUri().then((uri) {
      log("Initial deep link received: $uri");
      _onRedirected(uri);
    });
    stream.receiveBroadcastStream().listen((d) {
      log("StreamController has listeners: ${_stateController.hasListener}");
      log("Deep link received: $d");
      _onRedirected(d);
    }, onError: (e) {
      log("Deep link stream error: $e");
    });
  }


  void _onRedirected(String? uri) {
    if (uri != null) {
      stateSink.add(uri);
      print("Received a deep link.");
    } else {
      print("Received a null deep link.");
    }
  }


  @override
  void dispose() {
    _stateController.close();
    print("StreamController successfully closed.");
  }

  Future<String?> startUri() async {
    try {
      log("Invoking platform method: initialLink");
      return platform.invokeMethod('initialLink');
    } on PlatformException catch (e) {
      log("Initial deep link unavailable: ${e.message}");
      return null;
    }
  }
}
